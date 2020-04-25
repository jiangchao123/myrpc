package com.example.rpc.server.provider;

import com.example.rpc.core.Request;
import com.example.rpc.core.Response;
import com.example.rpc.registry.Registry;
import com.example.rpc.service.HelloService;
import com.example.rpc.service.impl.HelloServiceImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jiangchao08
 * @date 2020/4/25 6:49 下午
 */
public class Provider {

    private Map<String, Class> cache = new HashMap<String, Class>();

    public void start() {
        Registry.registerService();
        cache.put(HelloService.class.getName(), HelloServiceImpl.class);
        try {
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind(new InetSocketAddress("127.0.0.1", 39390));
            Selector selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务端开启了");

            while (true) {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocket.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        Request request = (Request) StreamUtil.readObject(socketChannel);
                        if (request != null) {
                            Object result = invoke(request);
                            Response response = new DefaultResponse();
                            response.setData(result);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(StreamUtil.readObject(response));
                            socketChannel.write(byteBuffer);
                        }
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器异常，即将关闭");
        }
    }

    private Object invoke(Request request) {
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] arguments = request.getArguments();

        Class clazz = cache.get(interfaceName);
        try {
            Object obj = clazz.newInstance();
            Method declaredMethod = clazz.getDeclaredMethod(methodName, parameterTypes);
            return declaredMethod.invoke(obj, arguments);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        new Provider().start();
    }
}
