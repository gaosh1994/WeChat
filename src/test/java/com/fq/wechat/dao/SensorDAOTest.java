package com.fq.wechat.dao;

import com.fq.wechat.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jifang.
 * @since 2016/4/3 15:35.
 */
public class SensorDAOTest extends TestBase {

    @Autowired
    private SensorDAO dao;

    @Test
    public void testUpdateContent() throws Exception {
        dao.updateSensorContentById("nihao", 1);
    }

    @Test
    public void testSelectContent() throws Exception {
        System.out.println(dao.selectSensorContentById(1));
    }
}