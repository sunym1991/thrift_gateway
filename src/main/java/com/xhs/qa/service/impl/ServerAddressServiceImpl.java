package com.xhs.qa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhs.qa.model.ZkNodeServiceData;
import com.xhs.qa.service.ServerAddressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.io.IOException;

/**
 * Created on 18/1/26 12:39
 *
 * @author sunyumei
 */
@Service
@Slf4j
public class ServerAddressServiceImpl implements ServerAddressService {
    private final CuratorFramework curatorFramework;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${thrifteasy.env}")
    private String env;

    private TreeCache treeCache;

    @Autowired
    public ServerAddressServiceImpl(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @PostConstruct
    private void init() throws Exception {
        if (!env.startsWith("/")) {
            env = "/" + env;
        }
        treeCache = new TreeCache(curatorFramework, env);
        treeCache.start();
    }


    @Override
    public String query(String name, String tags) {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        Map<String, ChildData> addresses = treeCache.getCurrentChildren(env + name);
        if (addresses == null) {
            return null;
        }
        if(tags != null){
            for (String key : addresses.keySet()) {
                try {
                    ZkNodeServiceData serviceData = objectMapper.readValue(addresses.get(key).getData(), ZkNodeServiceData.class);
                    if (serviceData.getTags() != null && serviceData.getTags().equals(tags)) {
                        return key;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>(addresses.keySet()).get(new Random().nextInt(addresses.size()));
    }
}