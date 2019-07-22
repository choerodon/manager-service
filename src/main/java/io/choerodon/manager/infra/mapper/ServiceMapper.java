package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dto.ServiceDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceMapper extends Mapper<ServiceDTO> {

    List<ServiceDTO> selectServicesByFilter(@Param("param") String param);
}
