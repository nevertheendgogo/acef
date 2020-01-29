package com.huangchao.acef.utils;

import lombok.Data;

/**
 * 本类为对controller返回结果的统一封装
 * 
 * @author huangchao
 *
 */
@Data
public class ResponseData {
	// 状态码
    private int status;
    // 消息,成功消息或者失败消息
    private String msg;
    // 要返回的数据
    private Object data;
    
	public ResponseData(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public ResponseData(Integer status, String msg, Object data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
    
}
