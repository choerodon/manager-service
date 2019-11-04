package io.choerodon.manager.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.shared.Application;
import io.choerodon.manager.api.dto.HostDTO;
import io.choerodon.manager.api.dto.register.ApplicationInfo;
import io.choerodon.manager.app.service.HostService;
import io.choerodon.manager.infra.retrofit.GoRegisterRetrofitClient;
import io.choerodon.manager.infra.utils.PageUtils;
import io.choerodon.manager.infra.utils.RetrofitCallExceptionParse;
import okhttp3.ResponseBody;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanghao
 * @Date 2019/11/4 10:17
 */
@Service
public class HostServiceImpl implements HostService {

    private static final String PROVISIONER = "provisioner";
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
            hostList = hostList.stream().filter(v -> v.getHostName().contains(hostName)).collect(Collectors.toList());
        }
        if (!ObjectUtils.isEmpty(ipAddr)) {
            hostList = hostList.stream().filter(v -> v.getIpAddr().contains(ipAddr)).collect(Collectors.toList());
        }
        if (!ObjectUtils.isEmpty(appName)) {
            hostList = hostList.stream().filter(v -> v.getAppName().contains(appName)).collect(Collectors.toList());
        }
        // 全局筛选
        if (!ObjectUtils.isEmpty(params)) {

        }
        return PageUtils.createPageFromList(hostList,pageable);
    }

    @Override
    public List<HostDTO> listHosts() {
        Call<ResponseBody> call = goRegisterRetrofitClient.listApps();
        List<Application> applicationList = new ArrayList<>();
        try {
            Response<ResponseBody> execute = call.execute();
            String string = execute.body().string();
            ApplicationInfo applicationInfo = objectMapper.readValue(string, ApplicationInfo.class);
            applicationList = applicationInfo.getApplications().getRegisteredApplications();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HostDTO> hostList = applicationList.stream().flatMap(application -> application.getInstances().stream()).map(v -> {
            HostDTO hostDTO = new HostDTO();
            hostDTO.setHostName(v.getHostName());
            hostDTO.setIpAddr(v.getIPAddr());
            hostDTO.setPort(v.getPort());
            hostDTO.setInstanceId(v.getInstanceId());
            hostDTO.setAppName(v.getAppName());
            hostDTO.setSourceType(v.getMetadata().get(PROVISIONER));
            return hostDTO;
        }).collect(Collectors.toList());
        return hostList;
    }

    @Override
    public void deleteHost(String appName, String instanceId) {
        Call<ResponseBody> call = goRegisterRetrofitClient.deleteApp(appName, instanceId);
        RetrofitCallExceptionParse.executeCall(call,"delete host failed : " + instanceId, Void.class);
    }
}
