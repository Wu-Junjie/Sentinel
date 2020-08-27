package com.alibaba.csp.sentinel.dashboard.repository.metric;

import com.alibaba.csp.sentinel.dashboard.dao.IMetricsDao;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.po.MetricPo;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author fw13
 * @date 2020/8/27 9:54
 */
@Component
public class MysqlMetricsRepository implements MetricsRepository<MetricEntity> {

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Autowired
    private IMetricsDao metricsDao;

    @Override
    public void save(MetricEntity entity) {
        if (entity == null || StringUtil.isBlank(entity.getApp())) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            MetricPo metricPo = new MetricPo();
            BeanUtils.copyProperties(entity, metricPo);

            metricsDao.insertMetric(metricPo);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            metrics.forEach(this::save);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();
        if (StringUtil.isBlank(app) || StringUtil.isBlank(resource)) {
            return results;
        }

        readWriteLock.readLock().lock();

        try {
            List<MetricPo> metricPos = metricsDao.queryByAppAndResourceBetween(app, resource, startTime / 1000, endTime / 1000);

            metricPos.forEach(metricPo -> {
                MetricEntity metricEntity = new MetricEntity();
                BeanUtils.copyProperties(metricPo, metricEntity);
                results.add(metricEntity);
            });

            return results;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }

        final long minTimeSec = System.currentTimeMillis() / 1000 - 60;

        readWriteLock.readLock().lock();
        try {
            return metricsDao.listResourcesOfApp(app, minTimeSec);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
