package io.choerodon.manager.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.mybatis.common.BaseMapper;

public interface ConfigMapper extends BaseMapper<ConfigDO> {

    ConfigDO selectOneByServiceDefault(@Param("serviceName") String serviceName);


    ConfigDO selectOneByServiceAndConfigVersion(@Param("serviceName") String serviceName,
                                                @Param("configVersion") String configVersion);

    int closeDefaultByServiceId(@Param("serviceId") Long serviceId);
}
