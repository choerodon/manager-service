package io.choerodon.manager.infra.mapper;

import java.util.List;

import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import io.choerodon.manager.infra.dataobject.SwaggerDO;

/**
 * @author zhipeng.zuo
 * @date 2018/1/24
 */
public interface SwaggerMapper extends Mapper<SwaggerDO> {

    /**
     * 获取服务所有版本
     *
     * @param service 查询的服务
     * @return 版本列表
     */
    @Select({"select version from swagger where service = #{mgmt_service}"})
    List<String> selectVersions(@Param("service") String service);
}
