package com.fq.wechat.domain;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author jifang.
 * @since 2016/4/14 15:21.
 */
public class SensorDO {

    @JSONField(name = "temperature")
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
