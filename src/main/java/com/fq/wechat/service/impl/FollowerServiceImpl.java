package com.fq.wechat.service.impl;

import com.fq.wechat.dao.FollowerDAO;
import com.fq.wechat.service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jifang.
 * @since 2016/4/3 15:47.
 */
@Service
public class FollowerServiceImpl implements FollowerService {

    @Autowired
    private FollowerDAO dao;

    public void addFollower(String follower) {
        dao.insertFollower(follower);
    }

    public void removeFollower(String follower) {
        dao.deleteFollower(follower);
    }

    public List<String> getAllFollower() {
        return dao.selectAllFollower();
    }
}
