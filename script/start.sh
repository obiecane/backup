#!/bin/bash
SERVER_NAME='jeecms-backup'
# jar名称
JAR_NAME='jeecms-backup-0.0.1-SNAPSHOT.jar'
cd `dirname $0`
BIN_DIR=`pwd`
DEPLOY_DIR=`pwd`

CONF_DIR=$DEPLOY_DIR/
# SERVER_PORT=`sed '/server.port/!d;s/.*=//' config/application.properties | tr -d '\r'`
# 获取应用的端口号
#SERVER_PORT=`sed -nr '/port: [0-9]+/ s/.*port: +([0-9]+).*/\1/p' config/application.yml`
SERVER_PORT=19889
PIDS=`ps -f | grep java | grep "$CONF_DIR" |awk '{print $2}'`

# 查询状态
if [ "$1" = "status" ]; then  
  if [ -n "$PIDS" ]; then
    echo "The $SERVER_NAME is running...!"
    echo "PID: $PIDS"
    exit 0
  else
    echo "The $SERVER_NAME is stopped"
    exit 0
  fi
fi
if [ -n "$PIDS" ]; then
  echo "ERROR: The $SERVER_NAME already started!"
  echo "PID: $PIDS"
  exit 1
fi
if [ -n "$SERVER_PORT" ]; then
  SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
  if [ $SERVER_PORT_COUNT -gt 0 ]; then
    echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
    exit 1
  fi
fi
LOGS_DIR=$DEPLOY_DIR
if [ ! -d $LOGS_DIR ]; then
  mkdir $LOGS_DIR
fi

STDOUT_FILE=$LOGS_DIR/`date +%Y_%m_%d_%H_%M_%S`.log
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "


# debug模式
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
  JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

#jmx
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
  JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

#jvm内存配置
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
  JAVA_MEM_OPTS=" -server -Xmx512m -Xms512m -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
  JAVA_MEM_OPTS=" -server -Xms512m -Xmx512m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

# application config file
#CONFIG_FILES=" -Dlogging.path=$LOGS_DIR -Dlogging.config=$CONF_DIR/log4j2.xml -Dspring.config.location=$CONF_DIR/application.properties "
CONFIG_FILES=" -Dspring.config.location=$CONF_DIR/application.properties "

echo -e "Starting the $SERVER_NAME ..."
# nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES -jar $DEPLOY_DIR/$JAR_NAME > $STDOUT_FILE 2>&1 &
nohup java $CONFIG_FILES -jar $DEPLOY_DIR/$JAR_NAME > $STDOUT_FILE 2>&1 &
COUNT=0
while [ $COUNT -lt 1 ]; do
  echo -e ".\c"
  sleep 1
  if [ -n "$SERVER_PORT" ]; then
    COUNT=`netstat -an | grep $SERVER_PORT | wc -l`
  else
   COUNT=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
  fi
  if [ $COUNT -gt 0 ]; then
    break
  fi
done
echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"