package io.choerodon.manager.api.eventhandler;

public class Test {

    @org.junit.Test
    public void yaml() {
        String yaml = "choerodon:\n" +
                "    gateway:\n" +
                "        helper:\n" +
                "            enabled: true\n" +
                "            enabled-jwt-log: false\n" +
                "            helper-skip-paths: /oauth/**, /manager/swagger-ui.html, /manager/swagger-resources/**, /manager/webjars/**, /manager/docs/**\n" +
                "            service-id: gateway-helper\n" +
                "eureka:\n" +
                "    client:\n" +
                "        disable-delta: true\n" +
                "        registryFetchIntervalSeconds: 10\n" +
                "        serviceUrl:\n" +
                "            defaultZone: http://localhost:8000/eureka/\n" +
                "    instance:\n" +
                "        leaseExpirationDurationInSeconds: 30\n" +
                "        leaseRenewalIntervalInSeconds: 10\n" +
                "        metadata-map:\n" +
                "            VERSION: v1\n" +
                "        preferIpAddress: true\n" +
                "hystrix:\n" +
                "    command:\n" +
                "        default:\n" +
                "            execution:\n" +
                "                isolation:\n" +
                "                    thread:\n" +
                "                        timeoutInMilliseconds: 20000\n" +
                "    stream:\n" +
                "        queue:\n" +
                "            enabled: true\n" +
                "management:\n" +
                "    port: 8081\n" +
                "    security:\n" +
                "        enabled: false\n" +
                "ribbon:\n" +
                "    ConnectTimeout: 20000\n" +
                "    ReadTimeout: 20000\n" +
                "    httpclient:\n" +
                "        enabled: false\n" +
                "    okhttp:\n" +
                "        enabled: true\n" +
                "security:\n" +
                "    basic:\n" +
                "        enabled: false\n" +
                "spring:\n" +
                "    cloud:\n" +
                "        bus:\n" +
                "            enabled: true\n" +
                "        stream:\n" +
                "            bindings:\n" +
                "                input:\n" +
                "                    destination: eureka-instance\n" +
                "            default-binder: kafka\n" +
                "            kafka:\n" +
                "                binder:\n" +
                "                    brokers: 127.0.0.1:9092\n" +
                "                    zkNodes: 127.0.0.1:2181\n" +
                "    http:\n" +
                "        multipart:\n" +
                "            max-file-size: 30MB\n" +
                "            max-request-size: 30MB\n" +
                "    sleuth:\n" +
                "        integration:\n" +
                "            enabled: false\n" +
                "        sampler:\n" +
                "            percentage: 1.0\n" +
                "        scheduled:\n" +
                "            enabled: false\n" +
                "        stream:\n" +
                "            enabled: true\n" +
                "zuul:\n" +
                "    addHostHeader: true\n" +
                "    semaphore:\n" +
                "        max-semaphores: 300\n" +
                "    sensitiveHeaders: Cookie,Set-Cookie\n";
        System.out.print(yaml);

    }
}
