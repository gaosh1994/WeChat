package com.fq.wechat.dao;

import java.util.List;

/**
 * @author jifang.
 * @since 2016/4/3 15:28.
 */
public interface FollowerDAO {

    void insertFollower(String follower);

    void deleteFollower(String follower);

    List<String> selectAllFollower();
}
