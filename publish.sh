## 下载代码
git pull

check_result $?

## 编译&打包
rm -rf target
mvn package

check_result $?

## 更新tomcat/webapps目录下的war包
rm -rf /usr/local/tomcat80/webapps/WeChat*
mv ./target/WeChat.war /usr/local/tomcat80/webapps/

check_result $?

## 重启tomcat
cd /usr/local/tomcat80/bin/
./shutdown.sh
./startup.sh

function check_result(){
    if  [ "$1" != "0" ]
        exit(-1)
}