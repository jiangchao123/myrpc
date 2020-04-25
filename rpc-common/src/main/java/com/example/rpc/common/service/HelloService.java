package com.example.rpc.common.service;

/**
 * @author jiangchao08
 * @date 2020/4/25 5:07 下午
 */
public interface HelloService {

    /**
     * sayHello
     * @param name
     */
    void sayHello(String name);

    /**
     * 获取名字
     * @return
     */
    String getName();
}
