package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMapper extends BaseMapper<ConfigDO> {

    ConfigDO selectOneByServiceDefault(@Param("serviceName") String serviceName);

    ConfigDO selectOneByServiceAndConfigVersion(@Param("serviceName") String serviceName,
                                                @Param("configVersion") String configVersion);

    List<ConfigDO> listByServiceName(@Param("serviceName") String serviceName);

    String selectConfigVersionById(@Param("id") Long id);
}
