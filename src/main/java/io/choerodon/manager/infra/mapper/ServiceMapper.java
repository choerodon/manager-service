package io.choerodon.manager.infra.mapper;

import io.choerodon.manager.infra.dataobject.ServiceDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceMapper extends Mapper<ServiceDO> {

    List<ServiceDO> selectServicesByFilter(@Param("param") String param);
}
