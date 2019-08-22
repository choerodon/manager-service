FROM registry.cn-shanghai.aliyuncs.com/choerodon-tools/javabase:0.8.2
COPY app.jar /manager-service.jar
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /manager-service.jar