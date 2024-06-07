package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lmwl
 * 官方的约束，即 默认 Nacos 适配的 dataId 和 groupId 约定如下：
 * <p>
 * groupId: SENTINEL_GROUP
 * 流控规则 dataId: {appName}-flow-rules，比如应用名为 appA，则 dataId 为 appA-flow-rules
 */
@ConfigurationProperties(prefix = "sentinel.nacos")
@Configuration
public class NacosPropertiesConfiguration {
    private String serverAddr;
    private String dataId;
    private String groupId = "SENTINEL_GROUP";
    private String namespace;

    private String username;

    private String password;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
