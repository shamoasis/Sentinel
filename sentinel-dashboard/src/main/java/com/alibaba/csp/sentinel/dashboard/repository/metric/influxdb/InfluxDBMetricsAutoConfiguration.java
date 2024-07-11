package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "sentinel.metrics.type", havingValue = "influxdb")
@EnableConfigurationProperties({InfluxDBMetricsProperties.class, OkHttpClientProperties.class})
@Configuration(proxyBeanMethods = false)
public class InfluxDBMetricsAutoConfiguration {
    private static final Logger log= LoggerFactory.getLogger(InfluxDBMetricsAutoConfiguration.class);

    public static final String AUTOWIRED_INFLUX_DB_METRICS_ENTITY_REPOSITORY = "Autowired InfluxDBMetricsEntityRepository";

    private final InfluxDBMetricsProperties influxDBMetricsProperties;

    private final InfluxDBConnect influxDBClient;

    public InfluxDBMetricsAutoConfiguration(InfluxDBMetricsProperties influxDBMetricsProperties,
                                            InfluxDBConnect influxDBClient) {
        this.influxDBMetricsProperties=influxDBMetricsProperties;
        this.influxDBClient=influxDBClient;
    }

    @Bean
    public InfluxDBMetricsEntityRepository influxDBMetricsEntityRepository() {
        log.debug(AUTOWIRED_INFLUX_DB_METRICS_ENTITY_REPOSITORY);
        return new InfluxDBMetricsEntityRepository(influxDBMetricsProperties, influxDBClient);
    }
}
