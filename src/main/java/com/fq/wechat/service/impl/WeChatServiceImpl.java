package com.fq.wechat.service.impl;

import com.fq.wechat.dao.WeChatDAO;
import com.fq.wechat.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jifang.
 * @since 2016/4/3 15:47.
 */
@Service
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private WeChatDAO dao;

    public void saveStatus(String status) {
        dao.updateContent(status, 1);
    }

    public String getStatus() {
        return dao.selectContent(1);
    }
}
