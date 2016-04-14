package com.fq.wechat.dao;

/**
 * @author jifang.
 * @since 2016/4/3 15:28.
 */
public interface SensorDAO {

    void updateSensorContentById(String content, Integer id);

    String selectSensorContentById(Integer id);
}
