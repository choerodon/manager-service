FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.7.0
COPY app.jar /manager-service.jar
COPY agent /agent
COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["entrypoint.sh"]