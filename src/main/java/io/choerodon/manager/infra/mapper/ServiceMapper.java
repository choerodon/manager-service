package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dataobject.ServiceDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceMapper extends BaseMapper<ServiceDO> {

    List<ServiceDO> selectServicesByFilter(@Param("param") String param);
}
