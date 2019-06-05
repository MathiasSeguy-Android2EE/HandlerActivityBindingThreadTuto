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

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to have a Runnable class that can be managed:
 *        <ul>
 *        <li>Can be set in the state "waitingToDie"</li>
 *        <li>Can be set in the state "Dying"</li>
 *        <li>Can be linked to a specific handler during process</li>
 *        </ul>
 */
public abstract class ManagedRunnable implements Runnable {
	/**
	 * The atomic boolean to wait to know if the thread should died or not
	 */
	public AtomicBoolean isThreadShouldDie = new AtomicBoolean(false);
	/**
	 * The atomic boolean to set the thread dead
	 */
	public AtomicBoolean setThreadDead = new AtomicBoolean(false);	
	/**
	 * The Handler targeted by the runnable (the runnable talks with this handler)
	 */
	public Handler runnableHandler;
}
