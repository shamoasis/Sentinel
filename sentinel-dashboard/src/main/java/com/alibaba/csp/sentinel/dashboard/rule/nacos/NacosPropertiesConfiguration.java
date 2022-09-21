package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lmwl
 * 官方的约束，即 默认 Nacos 适配的 dataId 和 groupId 约定如下：
 * <p>
 * groupId: SENTINEL_GROUP
 * 流控规则 dataId: {appName}-flow-rules，比如应用名为 appA，则 dataId 为 appA-flow-rules
 */
@ConfigurationProperties(prefix = "sentinel.nacos")
public class NacosPropertiesConfiguration {
    private String serverAddr;
    private String dataId;
    private String groupId = "SENTINEL_GROUP";
    private String namespace;

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
}
