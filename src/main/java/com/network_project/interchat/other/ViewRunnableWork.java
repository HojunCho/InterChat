package com.network_project.interchat.other;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public abstract class ViewRunnableWork implements Runnable {	
	private enum Status {
		IDLE(0), PLANNED(1), RUNNING(2), ALARMED(3);
		
		private final int value;
	    private Status(int value) {
	        this.value = value;
	    }
	    
	    public int getValue() {
	        return value;
	    }
	    
	    public final static AlarmStatus alarmStatus = new AlarmStatus();
	    public final static DealarmStatus dealarmStatus = new DealarmStatus();
	    
	    private static class AlarmStatus implements IntUnaryOperator {
			@Override
			public int applyAsInt(int arg0) {
				return arg0 | 1;
			}
	    }
	    
	    private static class DealarmStatus implements IntUnaryOperator {
			@Override
			public int applyAsInt(int arg0) {
				return arg0 & 1;
			}
	    }
	}
	
	private static ViewExecutor executor = new ViewExecutor(Math.min(Math.max(Runtime.getRuntime().availableProcessors()/2, 1), 16));
	
	public static void shutdown() {
		executor.shutdownNow();
	}
	
	private AtomicInteger state = new AtomicInteger(Status.IDLE.getValue());
	
	private void beforeExecute() {
		state.set(Status.RUNNING.getValue());
	}
	
	private void afterExecute() {
		if(state.getAndUpdate(Status.dealarmStatus) == Status.ALARMED.getValue())
			executor.execute(this);
	}
	
	public final void alarmExecutor() {
		if(state.getAndUpdate(Status.alarmStatus) == Status.IDLE.getValue())
			executor.execute(this);
	}
	
	private static class ViewExecutor extends ThreadPoolExecutor {
		public ViewExecutor(int pool_size) {
			super(pool_size, pool_size, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}
		
		@Override
		final protected void beforeExecute(Thread t, Runnable r) {
			((ViewRunnableWork)r).beforeExecute();
			super.beforeExecute(t, r);
		}
		
		@Override
		final protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			((ViewRunnableWork)r).afterExecute();
		}
	}
}
