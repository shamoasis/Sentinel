package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;


import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

/**
 * 监控数据实体模型
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
//@Builder
//@Data
@Measurement(name = "sentinel_metric")
public class InfluxDBMetricsEntity {

    @Column(name = "gmtCreate", tag = true)
    private Long gmtCreate;

    @Column(name = "gmtModified")
    private Long gmtModified;

    @Column(name = "app", tag = true)
    private String app;

    @Column(name = "time", tag = true)
    private Instant timestamp;

    @Column(name = "resource", tag = true)
    private String resource;

    @Column(name = "passQps", tag = true)
    private Long passQps;

    @Column(name = "successQps", tag = true)
    private Long successQps;

    @Column(name = "blockQps", tag = true)
    private Long blockQps;

    @Column(name = "exceptionQps", tag = true)
    private Long exceptionQps;

    @Column(name = "rt", tag = true)
    private double rt;

    @Column(name = "count", tag = true)
    private Integer count;

    @Column(name = "resourceCode", tag = true)
    private Integer resourceCode;

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Long getPassQps() {
        return passQps;
    }

    public void setPassQps(Long passQps) {
        this.passQps = passQps;
    }

    public Long getSuccessQps() {
        return successQps;
    }

    public void setSuccessQps(Long successQps) {
        this.successQps = successQps;
    }

    public Long getBlockQps() {
        return blockQps;
    }

    public void setBlockQps(Long blockQps) {
        this.blockQps = blockQps;
    }

    public Long getExceptionQps() {
        return exceptionQps;
    }

    public void setExceptionQps(Long exceptionQps) {
        this.exceptionQps = exceptionQps;
    }

    public double getRt() {
        return rt;
    }

    public void setRt(double rt) {
        this.rt = rt;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(Integer resourceCode) {
        this.resourceCode = resourceCode;
    }
}