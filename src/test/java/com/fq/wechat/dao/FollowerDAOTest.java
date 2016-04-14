package com.fq.wechat.dao;

import com.fq.wechat.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author jifang.
 * @since 2016/4/3 15:35.
 */
public class FollowerDAOTest extends TestBase {

    @Autowired
    private FollowerDAO dao;

    @Test
    public void testInsert() throws Exception {
        dao.insertFollower("nihao");
        dao.insertFollower("nihao1");
    }

    @Test
    public void testDelete() throws Exception{
        dao.deleteFollower("nihao");
    }

    @Test
    public void testSelectContent() throws Exception {
        List<String> list = dao.selectAllFollower();
        System.out.println(list);
    }
}