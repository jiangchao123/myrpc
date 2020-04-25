package com.example.rpc.server.service.impl;

import com.example.rpc.common.service.HelloService;

/**
 * @author jiangchao08
 * @date 2020/4/25 5:08 下午
 */
public class HelloServiceImpl implements HelloService {
    public void sayHello(String name) {
        System.out.println("hello " + name);
    }

    public String getName() {
        return "张三";
    }
}
