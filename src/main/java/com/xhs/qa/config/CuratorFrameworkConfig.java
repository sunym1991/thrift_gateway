package com.xhs.qa.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 18/1/23 13:45
 *
 * @author sunyumei
 */

@Configuration
@Slf4j
public class CuratorFrameworkConfig {

    @Value("${zookeeper.url}")
    private String zkUrl;

    @Value("${zookeeper.baseSleepTimeMs:1000}")
    private int zkBaseSleepTimeMs;

    @Value("${zookeeper.maxRetry:3}")
    private int zkMaxRetries;

    @Value("${zookeeper.namespace}")
    private String namespace;

    private CuratorFramework curatorFramework;

    @Bean
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkBaseSleepTimeMs, zkMaxRetries);
        curatorFramework = CuratorFrameworkFactory.newClient(zkUrl, retryPolicy);
        curatorFramework.start();
        return curatorFramework.usingNamespace(namespace);
    }
}

