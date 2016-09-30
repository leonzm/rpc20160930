package com.company.rpc.service.impl;

import com.company.rpc.annotation.RpcService;
import com.company.rpc.service.HelloService;

@RpcService(HelloService.class) // 指定远程接口
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "Hello! " + name;
	}

}
