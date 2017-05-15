package com.network_project.interchat.other;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * 주어진 일을 하는 에이전트
 */
public abstract class ViewRunnableWork implements Runnable {	
	/**
	 * 에이전트의 상태 정보가 선언된 열거형
	 */
	private enum Status {
		/** 아무것도 안하는 상태 */
		IDLE(0), 
		/** 일하기로 예약된 상태 */
		PLANNED(1), 
		/** 일하는 상태 */ 
		RUNNING(2), 
		/** 알람을 받은 상태 */
		ALARMED(3);
		
		private final int value;
	    private Status(int value) {
	        this.value = value;
	    }
	    
	    public int getValue() {
	        return value;
	    }
	    
	    /** 기본 알람 설정자. 기본 생성된다. */
	    public final static AlarmStatus alarmStatus = new AlarmStatus();
	    /** 기본 일 종료 설정자 . 기본 생성된다. */
	    public final static EndWorkStatus endWorkStatus = new EndWorkStatus();
	    
	    /**
	     * 알람을 받았을 때 이루어지는 상태 연산자
	     * from IDLE		to PLANNED
	     * from RUNNING		to ALARMED
	     */
	    private static class AlarmStatus implements IntUnaryOperator {
			@Override
			public int applyAsInt(int arg0) {
				return arg0 | 1;
			}
	    }
	    
	    /**
	     * 일이 종료되었을 떄 이루어지는 상태 연산자
	     * from RUNNING		to IDLE
	     * from ALARMED		to PLANED
	     */
	    private static class EndWorkStatus implements IntUnaryOperator {
			@Override
			public int applyAsInt(int arg0) {
				return arg0 & 1;
			}
	    }
	}
	
	/** 
	 * 일하기 에이전트를 위한 쓰레드 풀.
	 * 최대 16개, 최소 1개의 쓰레드가 생성된다.
	 * 기본적으로는 사용 가능한 프로세서 갯수의 1/2로 설정된다. 
	 */
	private static ViewExecutor executor = new ViewExecutor(Math.min(Math.max(Runtime.getRuntime().availableProcessors()/2, 1), 16));
	
	/**
	 * 모든 일하기 에이전트를 종료.
	 * 쓰레드 풀을 가동 중지한다. 
	 */
	public static void shutdown() {
		executor.shutdownNow();
	}
	
	/**
	 * 현재 일하기 에이전트의 상태
	 */
	private AtomicInteger state = new AtomicInteger(Status.IDLE.getValue());
	
	/**
	 * 에이전트가 일하기 직전 호출되는 메소드.
	 * 에이전트의 상태를 {@link Status#RUNNING 일하는 상태}로 만든다.
	 */
	private void beforeExecute() {
		state.set(Status.RUNNING.getValue());
	}
	
	/**
	 * 에이전트가 일을 완료한 후 호출되는 메소드.
	 * 에이전트의 상태를 일을 완료한 상태로 전환한다.
	 * 만약 전환하기 전 상태가 {@link Status#ALARMED 알람을 받은 상태}일 경우, 다시 쓰레드 풀에 에이전트를 예약한다. 
	 * @see Status#endWorkStatus
	 */
	private void afterExecute() {
		if(state.getAndUpdate(Status.endWorkStatus) == Status.ALARMED.getValue())
			executor.execute(this);
	}
	
	/**
	 * 에이전트를 깨운다.
	 * 에이전트의 상태를 알람을 받은 상태로 전환한다.
	 * 만약 전환하기 전 상태가 {@link Status#IDLE 아무것도 안하는 상태}일 경우, 쓰레드 풀에 에이전트를 예약한다.
	 * @see Status#alarmStatus
	 */
	public final void alarmExecutor() {
		if(state.getAndUpdate(Status.alarmStatus) == Status.IDLE.getValue())
			executor.execute(this);
	}
	
	/**
	 * 에이전트를 위한 쓰레드 풀.
	 */
	private static class ViewExecutor extends ThreadPoolExecutor {
		/**
		 * 쓰레드 풀의 생성자.
		 * @param pool_size 쓰레드 풀의 크기
		 */
		public ViewExecutor(int pool_size) {
			super(pool_size, pool_size, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}
		
		/**
		 * 쓰레드가 일하기 전, 해당하는 에이전트의 {@link ViewRunnableWork#beforeExecute() beforeExecute} 메소드를 실행시킨다. 
		 */
		@Override
		final protected void beforeExecute(Thread t, Runnable r) {
			((ViewRunnableWork)r).beforeExecute();
			super.beforeExecute(t, r);
		}
		
		/**
		 * 쓰레드가 일한 후, 해당하는 에이전트의 {@link ViewRunnableWork#afterExecute() afterExecute} 메소드를 실행시킨다.
		 */
		@Override
		final protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			((ViewRunnableWork)r).afterExecute();
		}
	}
}
