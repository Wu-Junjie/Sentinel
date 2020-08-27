package com.alibaba.csp.sentinel.dashboard.task;

import com.alibaba.csp.sentinel.dashboard.dao.IMetricsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author fw13
 * @date 2020/8/27 16:44
 */
@Component
public class ScheduleCleanMetrics {

    @Autowired
    private IMetricsDao metricsDao;

    /**
     * 每天0点 删除三天前数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    private void deleteTask(){
        metricsDao.cleanMetrics();
    }

}
