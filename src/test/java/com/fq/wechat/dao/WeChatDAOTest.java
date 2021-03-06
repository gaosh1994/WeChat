package com.fq.wechat.dao;

import com.fq.wechat.TestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author jifang.
 * @since 2016/4/3 15:35.
 */
public class WeChatDAOTest extends TestBase{

    @Autowired
    private WeChatDAO dao;

    @Test
    public void testUpdateContent() throws Exception {
        dao.updateContent("123", 1);
    }

    @Test
    public void testSelectContent() throws Exception {
        System.out.println(dao.selectContent(1));
    }
}