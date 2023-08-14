package com.pda.patrol.server.okhttp;

/***
 * 请求异常
 */
public class RequestException extends Exception {

 	private int mCode = 200;

	public RequestException(String msg) {
		super(msg);
	}

	public RequestException(String msg, int code) {
		this(msg);
		mCode = code;
	}

	public int getCode() {
		return mCode;
	}
}
