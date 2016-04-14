package com.fq.wechat.controller;

import com.fq.wechat.domain.SensorDO;
import com.fq.wechat.service.SensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jifang.
 * @since 2016/4/3 11:40.
 */
@Controller
@RequestMapping("/cc3200")
public class CC3200Controller {

    private Logger LOGGER = LoggerFactory.getLogger(CC3200Controller.class);

    @Autowired
    private SensorService service;

    @ResponseBody
    @RequestMapping("/sensor_status_add.do")
    public void sensorStatusAdd(SensorDO sensor) {
        service.saveSensorContent(sensor);
    }
}
