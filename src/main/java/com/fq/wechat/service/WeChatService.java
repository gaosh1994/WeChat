package com.fq.wechat.service;

/**
 * @author jifang.
 * @since 2016/4/3 15:46.
 */
public interface WeChatService {

    void saveStatus(String status);

    String getStatus();
}
