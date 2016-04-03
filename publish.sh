#!/bin/bash

## ���庯��
check_result() {
    if  [ "$1" != "0" ] ; then
        exit -1
    fi
}

## ���ش���
git pull

check_result $?

## ����&���
rm -rf target
mvn package -Dmaven.test.skip=true

check_result $?

## ����tomcat/webappsĿ¼�µ�war��
rm -rf /usr/local/tomcat80/webapps/WeChat*
mv ./target/WeChat.war /usr/local/tomcat80/webapps/

check_result $?

## ����tomcat
cd /usr/local/tomcat80/bin/
./shutdown.sh
./startup.sh