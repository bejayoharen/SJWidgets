/*
 * Created on Apr 18, 2004
 *
 */
package com.xowave.util;

//import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author bjorn
 *
 * Provides a Queue that manages background jobs. All Methods are thread safe.
 */
public class AsyncQueue extends Thread {
	private MyQueue<Runnable> queue;

	protected Runnable current;

	private MyQueue<Exception> exceptions;

	public AsyncQueue( boolean keepExceptions, AsyncQueue parent, String name, int priority ) {
		super(name);
		setPriority( priority );
		if (parent != null)
			queue = new MyQueue<Runnable>(parent.queue);
		else
			queue = new MyQueue<Runnable>();

		if (keepExceptions)
			exceptions = new MyQueue<Exception>();
	}
	public AsyncQueue(boolean keepExceptions) {
		this( keepExceptions, null, "Async Queue", Thread.NORM_PRIORITY );
	}

	/**
	 * if parent is non-null, this Thread inherits all non-run runnables from the parent's
	 * queue. Parent should be dead when this is called.
	 * 
	 * @param keepExceptions should exceptions thrown by runnables be saved
	 * @param parent the parent AsyncQueue who's unfinished runables will be inherited 
	 */
	public AsyncQueue(boolean keepExceptions, AsyncQueue parent) {
		this( keepExceptions, parent, "Async Queue", Thread.NORM_PRIORITY );
	}

	/**
	 * Adds a new runnable to the list.
	 */
	public final void push(Runnable r) {
		queue.push(r);
	}

	/**
	 * Called by Thread.start to start processing.
	 */
	@Override
	public final void run() {
		while (true) {
			// -- wait until there are objects Queued
			current = (Runnable) queue.get();
			//prerun
			preRun();
			try {
				current.run();
			} catch (Exception e) {
				//save it in case the user want it:
				if (exceptions != null)
					exceptions.push(e);
			}
			// postrun
			if (postRun())
				return;
			current = null;
		}
	}

	/**
	 * Subclasses may use this to perform some action before the current runnable
	 * is run. The variable current contains the runnable about to be run and may be
	 * accessed or altered.
	 */
	protected void preRun() {
	}

	/**
	 * Subclasses may use this to perform some action after the current runnable
	 * has been run. The variable current contains the runnable that was just run and may be
	 * accessed here.
	 * 
	 * if the subclass returns true, the thread will stop.
	 *
	 */
	protected boolean postRun() {
		return false;
	}

	/**
	 * Determines if there are any exceptions from past runs that are stored in the queue.
	 *
	 * @return true iff exceptions are being saved AND the next call to getException will return
	 * non-null.
	 */
	public synchronized boolean exceptionAvailable() {
		return exceptions != null && exceptions.available();
	}

	public synchronized Exception getException() {
		if (exceptions == null || !exceptions.available())
			return null;

		return (Exception) exceptions.get();
	}
	
	public boolean hasOperationsAvailable() {
		return queue.available();
	}
}
final class MyQueue<T> {
	private LinkedList<T> list;

	MyQueue() {
		list = new LinkedList<T>();
	}

	MyQueue(MyQueue<T> toCopy) {
		list = new LinkedList<T>(toCopy.list);
	}

	synchronized void push(T o) {
		list.add(o);
		notifyAll();
	}

	synchronized T get() {
		while (list.size() == 0) {
			try {
				wait();
			} catch (InterruptedException ie) {
			}
		}
		return list.remove(0);
	}

	synchronized boolean available() {
		return list.size() > 0;
	}
}
