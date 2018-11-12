package com.greatchn.bean;


import com.alibaba.fastjson.JSONObject;

public class Result {

	private boolean success = false;

	private String message = null;

	private Object bean = null;

	private Result() {

	}

	/**
	 * 消息结果是否有效
	 * 
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * 消息无效时反馈错误信息
	 * 
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 消息有效时反馈结果数据
	 * 
	 */
	public Object getBean() {
		return bean;
	}

	/**
	 * 生成一个有效消息
	 * 
	 * @param bean 成功返回数据
	 */
	public static Result success(Object bean) {
		Result result = new Result();
		result.success = true;
		result.message = null;
		result.bean = bean;
		return result;
	}

	/**
	 * 生成一个无效消息
	 * 
	 * @param message 错误信息
	 */
	public static Result fail(String message) {
		Result result = new Result();
		result.success = false;
		result.message = message;
		result.bean = null;
		return result;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
