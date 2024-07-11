package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = InfluxDBMetricsProperties.PREFIX)
public class InfluxDBMetricsProperties {
    public static final String PREFIX = "sentinel.metrics.influxdb";

    private String measurement = "sentinel_metric";
    private String url = "http://10.10.15.89:8086";
    private String username = "admin";
    private String password = "admin";
    private String retentionPolicy = "autogen";
    private String database = "s0";

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
