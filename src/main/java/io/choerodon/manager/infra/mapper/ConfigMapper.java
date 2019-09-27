package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dto.ConfigDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMapper extends Mapper<ConfigDTO> {

    List<ConfigDTO> selectByServiceDefault(@Param("serviceName") String serviceName);

    List<ConfigDTO> selectByServiceAndConfigVersion(@Param("serviceName") String serviceName,
                                         @Param("configVersion") String configVersion);

    List<ConfigDTO> fulltextSearch(@Param("config") ConfigDTO configDTO,
                                   @Param("serviceName") String serviceName,
                                   @Param("param") String param);

    List<String> selectConfigVersionById(@Param("id") Long id);
}
