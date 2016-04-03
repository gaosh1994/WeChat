package com.fq.wechat.controller;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author jifang.
 * @since 2016/4/3 11:40.
 */
@Controller
public class Test {

    private Logger log = LoggerFactory.getLogger(Test.class);
    private String Token = "feiqing";
    private String echostr;


    @RequestMapping("/connect.do")
    public void connect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        log.info("RemoteAddr: " + request.getRemoteAddr());
//        log.info("QueryString: " + request.getQueryString());
//        if (!accessing(request, response)) {
//            log.info("服务器接入失败.......");
//            return;
//        }
        //String echostr = getEchostr();
        echostr = request.getParameter("echostr");
        if (echostr != null && !"".equals(echostr)) {
            log.info("服务器接入生效..........");
            response.getWriter().print(echostr);//完成相互认证
        }
    }

    private boolean accessing(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if (isEmpty(signature)) {
            return false;
        }
        if (isEmpty(timestamp)) {
            return false;
        }
        if (isEmpty(nonce)) {
            return false;
        }
        if (isEmpty(echostr)) {
            return false;
        }
        String[] ArrTmp = {Token, timestamp, nonce};
        Arrays.sort(ArrTmp);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ArrTmp.length; i++) {
            sb.append(ArrTmp[i]);
        }
        String pwd = Encrypt(sb.toString());

        log.info("signature:" + signature + "timestamp:" + timestamp + "nonce:" + nonce + "pwd:" + pwd + "echostr:" + echostr);

        if (trim(pwd).equals(trim(signature))) {
            this.echostr = echostr;
            return true;
        } else {
            return false;
        }
    }

    private String Encrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(bt);
            strDes = bytes2Hex(md.digest()); //to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        return strDes;
    }

    public String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }


    public String getEchostr() {
        return echostr;
    }

    private boolean isEmpty(String str) {
        return null == str || "".equals(str) ? true : false;
    }

    private String trim(String str) {
        return null != str ? str.trim() : str;
    }

}
