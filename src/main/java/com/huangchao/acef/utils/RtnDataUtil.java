package com.huangchao.acef.utils;

public class RtnDataUtil {

	private RtnDataUtil() {
	}

	/** 操作成功 */
	public final static int SUCCESS_STATUS = 200;

	/** 操作失败 */
	public final static int FAIL_STATUS = 500;

	/** 操作成功 */
	public final static String SUCCESS_MSG = "success";

	/** 操作失败 */
	public final static String FAIL_MSG = "fail";

	// 操作成功带简单提示的返回封装
	private static ResponseData rtnSuccess = new ResponseData(SUCCESS_STATUS, "success");

	// 自定义失败返回数据
	public static ResponseData rtnErrorData(Object data) {
		return new ResponseData(FAIL_STATUS, FAIL_MSG, data);
	}

	// 自定义失败提示信息和返回数据
	public static ResponseData rtnErrorMSGData(String msg, Object data) {
		return new ResponseData(FAIL_STATUS, msg, data);
	}

	// 自定义失败所有返回数据
	public static ResponseData rtnErrorAll(int status, String msg, Object data) {
		return new ResponseData(status, msg, data);
	}

	// 仅返回简单成功提示
	public static ResponseData rtnSuccess() {
		return rtnSuccess;
	}

	// 可自定义设置成功提示
	public static ResponseData rtnSuccessMsg(String msg) {
		return new ResponseData(SUCCESS_STATUS, msg);
	}

	// 自定义成功返回数据
	public static ResponseData rtnSuccessData(Object data) {
		return new ResponseData(SUCCESS_STATUS, SUCCESS_MSG, data);
	}

	// 自定义成功提示信息和返回数据
	public static ResponseData rtnSuccessMSGData(String msg, Object data) {
		return new ResponseData(SUCCESS_STATUS, msg, data);
	}

	// 自定义成功所有返回数据
	public static ResponseData rtnSuccessAll(int status, String msg, Object data) {
		return new ResponseData(status, msg, data);
	}
}
