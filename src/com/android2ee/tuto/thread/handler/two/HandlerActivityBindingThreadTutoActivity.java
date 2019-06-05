/**<ul>
 * <li>HandlerActivityBindingThreadTuto</li>
 * <li>com.android2ee.tuto.thread.handler.two</li>
 * <li>15 déc. 2011</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.tuto.thread.handler.two;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to show ho to manage Activity's lifeCycle and Hanlder an thread's one
 *        using the binding effect:
 *        <ul>
 *        <li>The activity will retain the thread using getLastNonConfigurationInstance</li>
 *        <li>The class ManagedRunnable is used to stop the thread if the activity is not recreate
 *        immediately</li>
 *        </ul>
 *        The main point to handle here is: If your activity is recreate immediately, then the
 *        getLastNonConfigurationInstance will return your Thread
 *        Else it will return nothing. Until here it's look ok. Your problem is to stop the thread
 *        if the activity is not recreate immediately.
 *        Here it becomes the mess. To do that you need to be able to use AtomicBoolean within the
 *        Runnable... So You implement a new Runnable that you can manage in the activity. The
 *        problem is that this runnable will always point on the first handler that has been linked
 *        to it. So you need to link each time the activity is recreate the handler and the
 *        runnable.
 *        Ok, it's why there is so much to do for a simple consideration here.
 */
public class HandlerActivityBindingThreadTutoActivity extends Activity {
	/******************************************************************************************/
	/** Managing the Handler and the Thread *************************************************/
	/******************************************************************************************/
	/**
	 * The Handler
	 */
	private Handler handler;
	/**
	 * The thread that update the progressbar
	 */
	Thread backgroundThread;
	/**
	 * The runnable that have atomic booleans to be managed (and killed)
	 */
	ManagedRunnable runnable;

	/******************************************************************************************/
	/** Others attributes **************************************************************************/
	/******************************************************************************************/
	/**
	 * The string for the log
	 */
	private final static String TAG = "HandlerActivityBindingThreadTutoActivity";
	/**
	 * The ProgressBar
	 */
	private ProgressBar progressBar;
	/**
	 * The way the progress bar increment
	 */
	private boolean reverse = false;
	/**
	 * The activity name
	 */
	private String activityName;

	/******************************************************************************************/
	/** Managing the activity **************************************************************************/
	/******************************************************************************************/

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backgroundThread = (Thread) getLastNonConfigurationInstance();
		
		setContentView(R.layout.main);
		// Instantiate the progress bar
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setMax(100);
		// use a random double to give a name to the thread, the handler and the activity
		double randomD = Math.random();
		final int randomName = (int) (randomD * 100);
		activityName = "Activity" + randomName;
		Log.e(TAG, "The activity," + activityName + " is created");

		// handler definition
		handler = new Handler() {
			/**
			 * The handler name
			 */
			String handlerName = "HandlerName" + randomName;

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// retrieve the calling thread's name
				int threadId = (Integer) msg.getData().get("ThreadId");
				Log.e(TAG, "The handler," + handlerName + " receives a message from the thread n°" + threadId);
				// Launch treatment
				updateProgress();
			}
		};
		// Check if the thread is already running (if the activity have been destroyed and recreated
		// immediately after your thread should be alive)
		// The List<Object> contains the Runnable and the Thread
		@SuppressWarnings("unchecked")
		List<Object> objects = (List<Object>) getLastNonConfigurationInstance();
		// if the thread and the runnable are there:
		if (objects != null) {
			// load the runnable
			runnable = (ManagedRunnable) objects.get(0);
			// set the new handler to the runnable
			runnable.runnableHandler = handler;
			// load the thread
			backgroundThread = (Thread) objects.get(1);
			// then tell the thread it should not die
			runnable.isThreadShouldDie.set(false);
			// and do a log
			Log.e(TAG, "runnable and thread are restore");
		} else {
			// if the thread and the runnable are not there: create them
			// Define and implement the runnable
			runnable = new ManagedRunnable() {
				/**
				 * The message exchanged between this thread and the handler
				 */
				Message myMessage;

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					try {
						Log.e(TAG, "NewThread " + randomName);
						while (!setThreadDead.get()) {
							Log.e(TAG, "Thread is running (thread name" + randomName + ")");
							// For example sleep 0.1 second
							Thread.sleep(100);
							// Send the message to the handler (the
							// handler.obtainMessage is more
							// efficient that creating a message from scratch)
							// create a message, the best way is to use that
							// method. (take care here the runnableHandler is a field within the
							// ManagedRunnable class)
							myMessage = runnableHandler.obtainMessage();
							// put the thread id in the message to show which thread send it:
							Bundle data = new Bundle();
							data.putInt("ThreadId", randomName);
							myMessage.setData(data);
							// then send the message to the handler
							runnableHandler.sendMessage(myMessage);
							// Here is the smart element of the management
							// if the activity is not re-create immediately then kill your thread.
							if (isThreadShouldDie.get()) {
								// wait to see if the isThreadShouldDie boolean changes its value
								// wait 5s (for example)
								//ok, do 10 loops of 0.5s and while isThreadShouldDie is true
								for (int i = 0; (i < 10 )&& (isThreadShouldDie.get()); i++) {
									Thread.sleep(500);									
								}
								// then test again:
								if (isThreadShouldDie.get()) {
									//if it's still true, kill the thread
									setThreadDead.set(true);
								}
							}
						}
						Log.e(TAG, "Thread " + randomName + " dead");
					} catch (Throwable t) {
						// just end the background thread
					}
				}
			};
			//then link the Handler with the handler of the runnable
			runnable.runnableHandler = handler;
			//link the thread and the handler
			backgroundThread = new Thread(runnable);
			//set thread name
			backgroundThread.setName("HandlerTutoActivity " + randomName);
			// start the thread
			backgroundThread.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		// Save the Thread and the runnable
		List<Object> objects = new ArrayList<Object>();
		objects.add(runnable);
		objects.add(backgroundThread);
		return objects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onStop() {
		Log.i(TAG, "onStop called");
		// If the thread is not immediately restore then kill it
		runnable.isThreadShouldDie.set(true);
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		// Save the state of the reverse boolean
		outState.putBoolean("reverse", reverse);
		// then save the others GUI elements state
		super.onSaveInstanceState(outState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the state of the reverse boolean
		reverse = savedInstanceState.getBoolean("reverse");
		// then restore the others GUI elements state
		super.onRestoreInstanceState(savedInstanceState);
	}

	/******************************************************************************************/
	/** Private methods **************************************************************************/
	/******************************************************************************************/
	/**
	 * The method that update the progressBar
	 */
	private void updateProgress() {
		Log.e(TAG, "updateProgress called  (on activity n°" + activityName + ")");
		// get the current value of the progress bar
		int progress = progressBar.getProgress();
		// if the max is reached then reverse the progressbar's progress
		// if the 0 is reached then set the progressbar's progress normal
		if (progress == progressBar.getMax()) {
			reverse = true;
		} else if (progress == 0) {
			reverse = false;
		}
		// increment the progress bar according to the reverse boolean
		if (reverse) {
			progressBar.incrementProgressBy(-1);
		} else {
			progressBar.incrementProgressBy(1);
		}
	}
}