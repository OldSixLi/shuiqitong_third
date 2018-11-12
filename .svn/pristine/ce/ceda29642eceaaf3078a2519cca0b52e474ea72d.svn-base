package com.greatchn.common.exception;


/**
 * 全局异常
 * 
 * @author zy 2018-9-12
 * 
 */
public class GlobalExecption extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int exceptionId;

	private Throwable throwable;

	public int getExceptionId() {
		return exceptionId;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public GlobalExecption(int exceptionId, Throwable throwable) {
		super(throwable);
		this.exceptionId = exceptionId;
		this.throwable = throwable;
	}

}
