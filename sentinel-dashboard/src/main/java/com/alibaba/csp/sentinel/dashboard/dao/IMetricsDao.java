package com.alibaba.csp.sentinel.dashboard.dao;

import com.alibaba.csp.sentinel.dashboard.datasource.po.MetricPo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author fw13
 * @date 2020/8/27 10:50
 */
@Mapper
public interface IMetricsDao {

    /**
     * CREATE TABLE `sentinel_metric` (
     *   `id` INT NOT NULL AUTO_INCREMENT COMMENT 'id，主键',
     *   `gmt_create` DATETIME COMMENT '创建时间',
     *   `gmt_modified` DATETIME COMMENT '修改时间',
     *   `app` VARCHAR(100) COMMENT '应用名称',
     *   `timestamp` DATETIME COMMENT '统计时间',
     *   `resource` VARCHAR(500) COMMENT '资源名称',
     *   `pass_qps` INT COMMENT '通过qps',
     *   `success_qps` INT COMMENT '成功qps',
     *   `block_qps` INT COMMENT '限流qps',
     *   `exception_qps` INT COMMENT '发送异常的次数',
     *   `rt` DOUBLE COMMENT '所有successQps的rt的和',
     *   `_count` INT COMMENT '本次聚合的总条数',
     *   `resource_code` INT COMMENT '资源的hashCode',
     *   INDEX app_idx(`app`) USING BTREE,
     *   INDEX resource_idx(`resource`) USING BTREE,
     *   INDEX timestamp_idx(`timestamp`) USING BTREE,
     *   PRIMARY KEY (`id`)
     * ) ENGINE=INNODB DEFAULT CHARSET=utf8;
     */



    @Insert("INSERT INTO sentinel_metric (id,gmt_create,gmt_modified,app,timestamp," +
            "   resource,pass_qps,success_qps,block_qps,exception_qps,rt,count,resource_code) " +
            "values (#{id}, #{gmtCreate}, #{gmtModified},#{app},#{timestamp},#{resource}," +
            "   #{passQps},#{successQps},#{blockQps},#{exceptionQps},#{rt},#{count},#{resourceCode})")
    void insertMetric(MetricPo metric);

    @Select("select * from sentinel_metric where timestamp BETWEEN FROM_UNIXTIME(#{startTime}) " +
            "AND FROM_UNIXTIME(#{endTime}) AND app=#{app} AND resource=#{resource}")
    List<MetricPo> queryByAppAndResourceBetween(@Param("app")String app, @Param("resource")String resource, @Param("startTime")long startTime,@Param("endTime") long endTime);

    @Select("select DISTINCT resource from sentinel_metric where timestamp > FROM_UNIXTIME(#{minTimeSec}) and app=#{app}")
    List<String> listResourcesOfApp(@Param("app") String app, @Param("minTimeSec") long minTimeSec);

    @Delete("delete from sentinel_metric where timestamp < subdate(now(), interval 3 DAY)")
    void cleanMetrics();
}
