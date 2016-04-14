package com.fq.wechat.controller;

import com.fq.wechat.domain.SensorDO;
import com.fq.wechat.service.SensorService;
import com.fq.wechat.service.WeChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jifang.
 * @since 2016/4/3 11:40.
 */
@Controller
@RequestMapping("/cc3200")
public class CC3200Controller {

    private Logger LOGGER = LoggerFactory.getLogger(CC3200Controller.class);

    @Autowired
    private SensorService sService;

    @Autowired
    private WeChatService wService;


    @ResponseBody
    @RequestMapping("/sensor_status_add.do")
    public void sensorStatusAdd(SensorDO sensor, HttpServletResponse response) throws IOException {
        sService.saveSensorContent(sensor);
        response.getWriter().write(sensor.getTemperature());
    }

    @RequestMapping(value = "/get_status.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void getStatus(HttpServletResponse response) throws IOException {
        String status = wService.getStatus();
        response.getWriter().print(status);
    }

    @RequestMapping(value = "/upload_and_get.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void uploadAndGet(SensorDO sensor, HttpServletResponse response) throws IOException {
        sService.saveSensorContent(sensor);
        response.getWriter().print(wService.getStatus());
    }
}
