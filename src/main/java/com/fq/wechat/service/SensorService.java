package com.fq.wechat.service;

import com.fq.wechat.domain.SensorDO;

/**
 * @author jifang.
 * @since 2016/4/3 15:46.
 */
public interface SensorService {

    void saveSensorContent(SensorDO sensor);

    SensorDO getSensorContent();
}
