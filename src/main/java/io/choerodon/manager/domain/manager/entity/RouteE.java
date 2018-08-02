package io.choerodon.manager.domain.manager.entity;

import io.choerodon.manager.domain.repository.RouteRepository;
import io.choerodon.manager.infra.common.utils.VersionUtil;
import org.apache.commons.collections.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 路由领域对象
 *
 * @author superleader8@gmail.com
 * @author wuguokai
 */
@Component
@Scope("prototype")
public class RouteE {

    private Long id;

    private String name;

    private String path;

    private String serviceId;

    private String url;

    private Boolean stripPrefix;

    private Boolean retryable;

    private String sensitiveHeaders;

    private Boolean customSensitiveHeaders;

    private String helperService;

    private Long objectVersionNumber;

    private Boolean builtIn;

    @Autowired
    private RouteRepository routeRepository;

    /**
     * 通过路由名称获取对象
     *
     * @return RouteE
     */
    public RouteE getRouteByName() {
        return routeRepository.queryRoute(this);
    }

    /**
     * 通过服务id获取路由对象
     *
     * @return routeE
     */
    public RouteE getRouteByServiceId() {
        return routeRepository.queryRoute(this);
    }

    /**
     * 添加一个路由
     *
     * @return RouteE
     */
    public RouteE addRoute() {
        return routeRepository.addRoute(this);
    }

    /**
     * 更新一个路由
     *
     * @return RouteE
     */
    public RouteE updateRoute() {
        return routeRepository.updateRoute(this);
    }

    /**
     * 删除一个对象
     *
     * @return boolean
     */
    public boolean deleteRoute() {
        return routeRepository.deleteRoute(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(Boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public Boolean getCustomSensitiveHeaders() {
        return customSensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(Boolean customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    public String getHelperService() {
        return helperService;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        return "RouteE{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", path='" + path + '\''
                + ", serviceId='" + serviceId + '\''
                + ", url='" + url + '\''
                + ", stripPrefix=" + stripPrefix
                + ", retryable=" + retryable
                + ", sensitiveHeaders='" + sensitiveHeaders + '\''
                + ", customSensitiveHeaders=" + customSensitiveHeaders
                + ", helperService='" + helperService + '\''
                + ", objectVersionNumber=" + objectVersionNumber
                + ", routeRepository=" + routeRepository
                + '}';
    }
}
