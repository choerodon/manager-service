package io.choerodon.manager.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.manager.infra.dataobject.ServiceConfigDO;
import io.choerodon.mybatis.common.BaseMapper;

public interface ServiceConfigMapper extends BaseMapper<ServiceConfigDO> {
    ServiceConfigDO selectOneByServiceDefault(@Param("serviceName") String serviceName);


    ServiceConfigDO selectOneByServiceAndConfigVersion(@Param("serviceName") String serviceName,
                                                       @Param("configVersion") String configVersion);

    int closeDefaultByServiceId(@Param("serviceId") Long serviceId);
}
