package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Elasticsearch 监控实体数据访问仓库
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public class InfluxDBMetricsEntityRepository implements MetricsRepository<MetricEntity> {
    private static final DateTimeFormatter DATE_FORMAT_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final InfluxDBMetricsProperties influxDBMetricsProperties;

    private final InfluxDBConnect influxDBClient;

    public InfluxDBMetricsEntityRepository(InfluxDBMetricsProperties influxDBMetricsProperties,
                                           InfluxDBConnect influxDBClient) {
        this.influxDBMetricsProperties = influxDBMetricsProperties;
        this.influxDBClient = influxDBClient;
    }

    @Override
    public void save(MetricEntity metric) {
        if (metric == null || StringUtil.isBlank(metric.getApp())) {
            return;
        }
        Point point = convertToPoint(metric);

        influxDBClient.insert(influxDBMetricsProperties.getDatabase(), point);
    }

    /**
     * Save all metrics to the storage repository.
     *
     * @param metrics metrics to save
     */
    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        BatchPoints.Builder batchPoints = BatchPoints.database(influxDBMetricsProperties.getDatabase())
                .retentionPolicy(influxDBMetricsProperties.getRetentionPolicy());
        metrics.forEach(metric -> batchPoints.point(convertToPoint(metric)));

        influxDBClient.insert(batchPoints.build());
    }

    /**
     * Get all metrics by {@code appName} and {@code resourceName} between a period of time.
     *
     * @param app       application name for Sentinel
     * @param resource  resource name
     * @param startTime start timestamp
     * @param endTime   end timestamp
     * @return all metrics in query conditions
     */
    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        if (StringUtil.isBlank(resource)) {
            return results;
        }

        String sql = "SELECT * FROM " + influxDBMetricsProperties.getRetentionPolicy() + "." + influxDBMetricsProperties.getMeasurement() +
                " WHERE app=$app" +
                " AND resource=$resource" +
                " AND time>=$startTime" +
                " AND time<=$endTime";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app", app);
        paramMap.put("resource", resource);
        paramMap.put("startTime", toDateString(startTime));
        paramMap.put("endTime", toDateString(endTime));

        List<InfluxDBMetricsEntity> metricPOS = influxDBClient.queryList(influxDBMetricsProperties.getDatabase(),
                sql, paramMap, InfluxDBMetricsEntity.class);

        if (CollectionUtils.isEmpty(metricPOS)) {
            return results;
        }

        for (InfluxDBMetricsEntity metricPO : metricPOS) {
            results.add(convertToMetricEntity(metricPO));
        }

        return results;
    }

    private String toDurationStr(long selected, Instant now) {
        Instant passed = Instant.ofEpochMilli(selected);
        long sec = Duration.between(now, passed).getSeconds();
        if (sec > 0) {
            return "now()";
        }
        return sec + "s";
    }

    /**
     * List resource name of provided application name.
     *
     * @param app application name
     * @return list of resources
     */
    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }

        String sql = "SELECT * FROM " + influxDBMetricsProperties.getRetentionPolicy() + "." + influxDBMetricsProperties.getMeasurement() +
                " WHERE app=$app" +
                " AND time>=$startTime";

        Map<String, Object> paramMap = new HashMap<>();
        long startTime = System.currentTimeMillis() - 1000 * 300;
        paramMap.put("app", app);
        paramMap.put("startTime", toDateString(startTime));

        List<InfluxDBMetricsEntity> metricPOS = influxDBClient.queryList(influxDBMetricsProperties.getDatabase(), sql, paramMap, InfluxDBMetricsEntity.class);

        if (CollectionUtils.isEmpty(metricPOS)) {
            return results;
        }

        List<MetricEntity> metricEntities = new ArrayList<>();
        for (InfluxDBMetricsEntity metricPO : metricPOS) {
            metricEntities.add(convertToMetricEntity(metricPO));
        }

        Map<String, MetricEntity> resourceCount = new HashMap<>(32);

        for (MetricEntity metricEntity : metricEntities) {
            String resource = metricEntity.getResource();
            if (resourceCount.containsKey(resource)) {
                MetricEntity oldEntity = resourceCount.get(resource);
                oldEntity.addPassQps(metricEntity.getPassQps());
                oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
                oldEntity.addBlockQps(metricEntity.getBlockQps());
                oldEntity.addExceptionQps(metricEntity.getExceptionQps());
                oldEntity.addCount(1);
            } else {
                resourceCount.put(resource, MetricEntity.copyOf(metricEntity));
            }
        }

        // Order by last minute b_qps DESC.
        return resourceCount.entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    MetricEntity e1 = o1.getValue();
                    MetricEntity e2 = o2.getValue();
                    int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                    if (t != 0) {
                        return t;
                    }
                    return e2.getPassQps().compareTo(e1.getPassQps());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static MetricEntity convertToMetricEntity(InfluxDBMetricsEntity metricsEntity) {
        MetricEntity entity = new MetricEntity();
        entity.setApp(metricsEntity.getApp());
        entity.setResource(metricsEntity.getResource());
        entity.setBlockQps(metricsEntity.getBlockQps());
        entity.setCount(metricsEntity.getCount());
        entity.setExceptionQps(metricsEntity.getExceptionQps());
        entity.setPassQps(metricsEntity.getPassQps());
        entity.setSuccessQps(metricsEntity.getSuccessQps());
        entity.setRt(metricsEntity.getRt());
        entity.setTimestamp(toDate(metricsEntity.getTimestamp().toEpochMilli()));
        entity.setGmtCreate(toDate(metricsEntity.getGmtCreate()));
        entity.setGmtModified(toDate(metricsEntity.getGmtModified()));
        return entity;
    }

    private Point convertToPoint(MetricEntity metric) {
        if (metric.getId() == null) {
            metric.setId(System.currentTimeMillis());
        }
        Point point = Point.measurement(influxDBMetricsProperties.getMeasurement())
                .time(metric.getTimestamp().getTime(), TimeUnit.MILLISECONDS)
                .tag("app", metric.getApp())
                .tag("resource", metric.getResource())
                .addField("id", metric.getId())
                .addField("gmtCreate", metric.getGmtCreate().getTime())
                .addField("gmtModified", metric.getGmtModified().getTime())
                .addField("passQps", metric.getPassQps())
                .addField("successQps", metric.getSuccessQps())
                .addField("blockQps", metric.getBlockQps())
                .addField("exceptionQps", metric.getExceptionQps())
                .addField("rt", metric.getRt())
                .addField("count", metric.getCount())
                .addField("resourceCode", metric.getResourceCode())
                .build();

        return point;
    }

    private static Date toDate(Long time) {
        if (null != time) {
            return new Date(time);
        }
        return new Date();
    }

    private String toDateString(long millSeconds) {
        return DATE_FORMAT_PATTERN.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(millSeconds), ZoneOffset.UTC));
    }
}