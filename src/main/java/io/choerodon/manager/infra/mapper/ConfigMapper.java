package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dataobject.ConfigDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMapper extends Mapper<ConfigDO> {

    List selectByServiceDefault(@Param("serviceName") String serviceName);

    List selectByServiceAndConfigVersion(@Param("serviceName") String serviceName,
                                         @Param("configVersion") String configVersion);

    List<ConfigDO> fulltextSearch(@Param("config") ConfigDO configDO,
                                  @Param("serviceName") String serviceName,
                                  @Param("param") String param);

    List<String> selectConfigVersionById(@Param("id") Long id);
}
