package com.cardinfolink.yunshouyin.util;

public class ErrorUtil {
  
	public static String getErrorString(String error){
		if(error.equals("user_no_activate")){
			return "账号未激活!";
		}
		
		if(error.equals("username_password_error")){
			return "用户名或密码错误!";
		}
		
		if(error.equals("sign_fail")){
			return "签名错误,报文被串改!";
		}
		
		if(error.equals("username_no_exist")){
			return "用户名不存在!";
		}
		
		if(error.equals("username_exist")){
			return "用户名已存在!";
		}
		
		if(error.equals("system_error")){
			return "对不起,系统出现错误!";
		}
		if(error.equals("old_password_error")){
			return "原密码错误!";
		}
		
		return error;
		
	}
	
	
	
	
}