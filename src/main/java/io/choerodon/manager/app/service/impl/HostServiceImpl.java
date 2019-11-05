package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import io.choerodon.core.exception.CommonException;
import io.choerodon.manager.api.dto.HostDTO;
import io.choerodon.manager.api.dto.HostVO;
import io.choerodon.manager.api.dto.HostWarpPortDTO;
import io.choerodon.manager.api.dto.register.ApplicationInfo;
import io.choerodon.manager.app.service.HostService;
import io.choerodon.manager.infra.retrofit.GoRegisterRetrofitClient;
import io.choerodon.manager.infra.utils.PageUtils;
import okhttp3.ResponseBody;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanghao
 * @Date 2019/11/4 10:17
 */
@Service
public class HostServiceImpl implements HostService {

    private static final String PROVISIONER = "provisioner";
    private static final String HOST_NAME = "hostName";
    private static final String PORT_ENABLE = "@enabled";
    private static final String PORT = "$";
    private static final String ROUTE_RULE_CODE = "routeRuleCode";
    private GoRegisterRetrofitClient goRegisterRetrofitClient;
    private ObjectMapper objectMapper;


    public HostServiceImpl(GoRegisterRetrofitClient goRegisterRetrofitClient, ObjectMapper objectMapper) {
        this.goRegisterRetrofitClient = goRegisterRetrofitClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageInfo<HostDTO> pagingHosts(String sourceType, String hostName, String ipAddr, Integer port, String appName, String[] params, Pageable pageable) {
        List<HostDTO> hostList = listHosts();
        // 过滤
        if (!ObjectUtils.isEmpty(sourceType)) {
            hostList = hostList.stream().filter(v -> v.getSourceType().equals(sourceType)).collect(Collectors.toList());
        }
        if (!ObjectUtils.isEmpty(port)) {
            hostList = hostList.stream().filter(v -> port.equals(v.getPort())).collect(Collectors.toList());
        }

        if (!ObjectUtils.isEmpty(hostName)) {
            hostList = hostList.stream().filter(v -> v.getMetadata().get(HOST_NAME) != null && v.getMetadata().get(HOST_NAME).contains(hostName)).collect(Collectors.toList());
        }
        if (!ObjectUtils.isEmpty(ipAddr)) {
            hostList = hostList.stream().filter(v -> v.getIpAddr().contains(ipAddr)).collect(Collectors.toList());
        }
        if (!ObjectUtils.isEmpty(appName)) {
            hostList = hostList.stream().filter(v -> v.getAppName().contains(appName)).collect(Collectors.toList());
        }
        return PageUtils.createPageFromList(hostList, pageable);
    }

    @Override
    public List<HostDTO> listHosts() {
        List<HostDTO> hostList = new ArrayList<>();
        Call<ResponseBody> call = goRegisterRetrofitClient.listApps();
        ApplicationInfo applicationInfo = executeRetrofitCall(call, ApplicationInfo.class, "list hosts failed");
        if (applicationInfo == null) {
            return hostList;
        }
        Applications applications = applicationInfo.getApplications();
        if (applications == null) {
            return hostList;
        }
        List<Application> applicationList = applications.getRegisteredApplications();
        hostList = applicationList.stream().flatMap(application -> application.getInstances().stream()).map(v -> {
            HostDTO hostDTO = new HostDTO();
            hostDTO.setHostName(v.getHostName());
            hostDTO.setIpAddr(v.getIPAddr());
            hostDTO.setPort(v.getPort());
            hostDTO.setInstanceId(v.getInstanceId());
            hostDTO.setAppName(v.getAppName());
            hostDTO.setSourceType(v.getMetadata().get(PROVISIONER));
            hostDTO.setRouteRuleCode(v.getMetadata().get(ROUTE_RULE_CODE));
            hostDTO.setCreateDate(new Date(v.getLeaseInfo().getRegistrationTimestamp()));
            hostDTO.setMetadata(v.getMetadata());
            return hostDTO;
        }).collect(Collectors.toList());
        return hostList;
    }

    @Override
    public void deleteHost(String appName, String instanceId) {
        Call<ResponseBody> call = goRegisterRetrofitClient.deleteHost(appName, instanceId);
        executeRetrofitCall(call, "delete host failed : " + instanceId);
    }

    @Override
    public void saveHost(String appName, HostVO hostVO) {
        saveHost(appName, hostVO, "save host failed : " + hostVO);
    }

    private void saveHost(String appName, HostVO hostVO, String erroMsg) {
        HostWarpPortDTO host = new HostWarpPortDTO();
        Map<String, String> metadata = new HashMap<>();
        metadata.put(HOST_NAME, hostVO.getHostName());
        host.setMetadata(metadata);
        host.setIpAddr(hostVO.getIpAddr());
        Map<String, Object> map = new HashMap<>();
        map.put(PORT_ENABLE, true);
        map.put(PORT, hostVO.getPort());
        host.setPort(map);
        host.setStatus("UP");
        Call<ResponseBody> call = goRegisterRetrofitClient.createHost(appName, host);
        executeRetrofitCall(call, erroMsg);
    }

    private <T> T executeRetrofitCall(Call<ResponseBody> call, Class<T> clazz, String erroMsg) {
        try {
            Response<ResponseBody> execute = call.execute();
            if (!execute.isSuccessful()) {
                throw new CommonException(erroMsg);
            }
            if (execute.body() == null) {
                return null;
            }
            String string = execute.body().string();
            return objectMapper.readValue(string, clazz);
        } catch (IOException e) {
            throw new CommonException(erroMsg);
        }
    }

    private void executeRetrofitCall(Call<ResponseBody> call, String erroMsg) {
        try {
            Response<ResponseBody> execute = call.execute();
            if (!execute.isSuccessful()) {
                throw new CommonException(erroMsg);
            }
        } catch (IOException e) {
            throw new CommonException(erroMsg);
        }
    }
}
