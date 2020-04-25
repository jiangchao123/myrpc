package com.example.rpc.client.consumer;

import com.example.rpc.client.Transport;
import com.example.rpc.common.core.Request;
import com.example.rpc.common.core.Response;
import com.example.rpc.common.registry.Registry;
import com.example.rpc.common.service.HelloService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author jiangchao08
 * @date 2020/4/25 7:54 下午
 */
public class Consumer {

    public <T> T getService(Class<T> clazz) {
        return <T> Proxy.newProxyInstance(Consumer.class.getClassLoader(), new Class[](clazz), new MyInvocationHandler(clazz));
    }


    private class MyInvocationHandler implements InvocationHandler {

        Class clazz;

        public MyInvocationHandler(Class clazz) {
            this.clazz = clazz;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Request request = buildRequest(method, args, clazz);
            return getResult(request).getData();
        }
    }

    private Response getResult(Request request) {
        Transport transport = new Transport();
        return transport.transport(request);
    }

    private Request buildRequest(Method method, Object[] args, Class clazz) {
        Request request = new DefaultRequest();
        request.setInterfaceName(clazz.getName());
        request.setMethodName(method.getName());
        request.setArguments(args);
        request.setParameterTypes(method.getTypeParameters());
        return request;
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        Registry.registerService();
        HelloService service = consumer.getService(HelloService.class);
        service.sayHello("world");
        System.out.println(service.getName());
    }
}
