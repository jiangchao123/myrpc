package com.example.rpc.common.core;

/**
 * @author jiangchao08
 * @date 2020/4/25 5:10 下午
 */
public interface Request {

    String getInterfaceName();

    String getMethodName();

    Object[] getArguments();

    Class<?>[] getParameterTypes();
}
