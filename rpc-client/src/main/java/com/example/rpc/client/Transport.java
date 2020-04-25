package com.example.rpc.client;

import com.example.rpc.common.core.Request;
import com.example.rpc.common.core.Response;
import com.example.rpc.common.registry.Registry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author jiangchao08
 * @date 2020/4/25 7:37 下午
 */
public class Transport {

    public Response transport(Request request) {
        Map<String, Object> serviceInfo = Registry.getServiceInfo(request.getInterfaceName());
        String host = (String) serviceInfo.get("host");
        Integer port = (Integer) serviceInfo.get("port");
        Response response = null;

        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(host, port));
            while (true) {
                if (socketChannel.isOpen()) {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isConnectable()) {
                            while (!socketChannel.finishConnect()) {
                                System.out.println("连接中");
                            }
                            socketChannel.register(selector, SelectionKey.OP_WRITE);
                        }
                        if (key.isWritable()) {
                            byte[] bytes = StreamUtil.readObject(request);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                            socketChannel.write(byteBuffer);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        if (key.isReadable()) {
                            response = (Response) StreamUtil.readObject(socketChannel);
                        }
                    }
                    if (response != null) {
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端异常,请重启");
        }
        return response;
    }
}
