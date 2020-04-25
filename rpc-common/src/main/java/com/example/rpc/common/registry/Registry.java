package com.example.rpc.common.registry;

import com.example.rpc.common.service.HelloService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangchao08
 * @date 2020/4/25 6:39 下午
 */
public class Registry {

    private static Map<String, Map<String, Object>> register = new HashMap<String, Map<String, Object>>();

    public static void addService(String serviceName, Map map) {
        register.put(serviceName, map);
    }

    public static void registerService() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("host", "127.0.0.1");
        info.put("port", 39390);
        Registry.addService(HelloService.class.getName(), info);
    }
}
