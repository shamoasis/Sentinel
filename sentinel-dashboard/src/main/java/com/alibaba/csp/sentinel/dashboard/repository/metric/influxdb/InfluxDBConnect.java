package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "sentinel.metrics.type", havingValue = "influxdb")
public class InfluxDBConnect {
    private static final Logger log = LoggerFactory.getLogger(InfluxDBConnect.class);

    private final InfluxDBMetricsProperties influxDBMetricsProperties;
    private final OkHttpClientProperties okHttpClientProperties;
    private InfluxDB influxDB;
    private InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

    public InfluxDBConnect(InfluxDBMetricsProperties influxDBMetricsProperties, OkHttpClientProperties okHttpClientProperties) {
        this.influxDBMetricsProperties = influxDBMetricsProperties;
        this.okHttpClientProperties = okHttpClientProperties;
        build();
    }

    private void build() {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(okHttpClientProperties.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(okHttpClientProperties.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(okHttpClientProperties.getWriteTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(okHttpClientProperties.getRetryOnConnectionFailure());
        if (influxDBMetricsProperties.getUsername() == null || influxDBMetricsProperties.getPassword() == null) {
            influxDB = InfluxDBFactory.connect(influxDBMetricsProperties.getUrl(), okHttpClient);
        } else {
            influxDB = InfluxDBFactory.connect(influxDBMetricsProperties.getUrl(),
                    influxDBMetricsProperties.getUsername(), influxDBMetricsProperties.getPassword(), okHttpClient);
        }
        Pong pong = influxDB.ping();
        if (pong != null) {
            log.info("pong：" + pong + ",连接成功！");
        } else {
            log.info("连接失败");
        }

    }

    public void insert(String dbName, Point point) {
        influxDB.write(dbName, influxDBMetricsProperties.getRetentionPolicy(), point);
    }

    public void insert(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }

    public <T> List<T> queryList(String database, String query, Class<T> clazz) {
        QueryResult queryResult = influxDB.query(new Query(query, database));
        return resultMapper.toPOJO(queryResult, clazz);
    }

    public <T> List<T> queryList(String database, String query, Map<String, Object> paramMap, Class<T> clazz) {
        BoundParameterQuery.QueryBuilder queryBuilder = BoundParameterQuery.QueryBuilder.newQuery(query);
        queryBuilder.forDatabase(database);

        if (paramMap != null && !paramMap.isEmpty()) {
            Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                queryBuilder.bind(entry.getKey(), entry.getValue());
            }
        }

        QueryResult queryResult = influxDB.query(queryBuilder.create());
        return resultMapper.toPOJO(queryResult, clazz);
    }

}
