#!/bin/bash

## 定义函数
check_result() {
    if  [ "$1" != "0" ] ; then
        exit -1
    fi
}

## 下载代码
git pull

check_result $?

## 编译&打包
rm -rf target
mvn package -Dmaven.test.skip=true

check_result $?

## 更新tomcat/webapps目录下的war包
rm -rf /usr/local/tomcat80/webapps/WeChat*
mv ./target/WeChat.war /usr/local/tomcat80/webapps/

check_result $?

## 重启tomcat
cd /usr/local/tomcat80/bin/
./shutdown.sh
./startup.sh