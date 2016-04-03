package com.fq.wechat.dao;

/**
 * @author jifang.
 * @since 2016/4/3 15:28.
 */
public interface WeChatDAO {

    void updateContent(String content, Integer id);

    String selectContent(Integer id);
}
