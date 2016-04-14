package com.fq.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fq.wechat.dao.SensorDAO;
import com.fq.wechat.domain.SensorDO;
import com.fq.wechat.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jifang.
 * @since 2016/4/3 15:47.
 */
@Service
public class SensorServiceImpl implements SensorService {

    @Autowired
    private SensorDAO dao;

    public void saveSensorContent(SensorDO sensor) {
        String str = JSON.toJSONString(sensor);
        dao.updateSensorContentById(str, 1);
    }

    public SensorDO getSensorContent() {
        String json = dao.selectSensorContentById(1);
        SensorDO sensor = JSONObject.parseObject(json, SensorDO.class);
        return sensor;
    }
}
