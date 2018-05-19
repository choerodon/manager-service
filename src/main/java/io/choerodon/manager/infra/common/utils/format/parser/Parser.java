package io.choerodon.manager.infra.common.utils.format.parser;

import java.io.IOException;
import java.util.Map;

/**
 * 解析器接口
 *
 * @author wuguokai
 */
public interface Parser {
    /**
     * 解析文件获取配置项信息
     *
     * @param content 配置内容
     * @return 返回property-value键值对
     * @throws IOException 解析时发生的异常
     */
    Map<String, Object> parse(String content) throws IOException;
}
