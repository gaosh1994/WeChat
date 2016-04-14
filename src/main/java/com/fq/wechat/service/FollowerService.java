package com.fq.wechat.service;

import java.util.List;

/**
 * @author jifang.
 * @since 2016/4/3 15:46.
 */
public interface FollowerService {

    void addFollower(String follower);

    void removeFollower(String follower);

    List<String> getAllFollower();
}
