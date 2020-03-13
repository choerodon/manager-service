package io.choerodon.manager.infra.common.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * list手动分页工具类
 *
 * @author flyleft
 * @author superlee
 */
public class ManualPageHelper {

    private static final Logger logger = LoggerFactory.getLogger(ManualPageHelper.class);

    private static final String PARAMS_KEY = "params";

    private ManualPageHelper() {
    }

    public static <T> PageInfo<T> postPage(final List<T> source, Pageable pageable, final Map<String, Object> filters) {
        return postPage(source, pageable.getPageNumber(), pageable.getPageSize(), filters, defaultCompare(pageable.getSort()));
    }

    public static <T> PageInfo<T> postPage(final List<T> source, int page, int size,
                                           final Map<String, Object> filters, Comparator<T> comparable) {
        try (Page<T> result = new Page<>(page, size)) {
            final List<T> filterList = source.stream().filter(t -> throughFilter(t, filters))
                    .sorted(comparable).collect(Collectors.toList());
            List<T> pageList = getPageList(page, size, filterList);
            result.setTotal(filterList.size());
            result.addAll(pageList);
            return result.toPageInfo();
        }
    }

    private static <T> Comparator<T> defaultCompare(final Sort sort) {
        return (T o1, T o2) -> {
            Iterator<Sort.Order> iterator = sort.iterator();
            if (iterator.hasNext()) {
                Sort.Order order = iterator.next();
                String property = order.getProperty();
                Class<?> objClass = o1.getClass();
                try {
                    Field field = objClass.getDeclaredField(property);
                    field.setAccessible(true);
                    if (field.getType().equals(String.class)) {
                        if (order.getDirection().isAscending()) {
                            return ((String) field.get(o1)).compareTo((String) field.get(o2));
                        } else {
                            return ((String) field.get(o2)).compareTo((String) field.get(o1));
                        }
                    }
                    if (field.getType().equals(Integer.class)
                            || field.getType().equals(Short.class)
                            || field.getType().equals(Character.class)
                            || field.getType().equals(Byte.class)) {
                        if (order.getDirection().isAscending()) {
                            return Integer.compare((Integer) field.get(o1), (Integer) field.get(o2));
                        } else {
                            return Integer.compare((Integer) field.get(o2), (Integer) field.get(o1));
                        }
                    }
                    if (field.getType().equals(Double.class) || field.getType().equals(Float.class)) {
                        if (order.getDirection().isAscending()) {
                            return Double.compare((Double) field.get(o1), (Double) field.get(o2));
                        } else {
                            return Double.compare((Double) field.get(o2), (Double) field.get(o1));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    logger.debug("error in compare {}", e.getMessage());
                }
            }
            return 0;
        };
    }

    private static <T> List<T> getPageList(int page, int pageSize, List<T> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        int totalCount = source.size();
        page = page <= 1 ? 1 : page;
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= totalCount) {
            return Collections.emptyList();
        }
        int toIndex = page * pageSize;
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return source.subList(fromIndex, toIndex);
    }

    private static <T> boolean throughFilter(final T obj, final Map<String, Object> filters) {
        Class<?> objClass = obj.getClass();
        boolean allIsNullExcludeParams = true;
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            if (!PARAMS_KEY.equals(key) && !StringUtils.isEmpty(entry.getValue())) {
                allIsNullExcludeParams = false;
                break;
            }
        }
        final Object params = filters.get(PARAMS_KEY);
        if (params != null && allIsNullExcludeParams) {
            return filters.entrySet().stream()
                    .filter(t -> !t.getKey().equals(PARAMS_KEY))
                    .anyMatch(i -> paramThrough(objClass, obj, i.getKey(), params));
        } else {
            return filters.entrySet().stream()
                    .filter(t -> t.getValue() != null).noneMatch(i -> notThrough(objClass, obj, i));
        }
    }

    private static <T> boolean paramThrough(final Class<?> objClass, final T obj, final String key, final Object params) {
        try {
            Field field = objClass.getDeclaredField(key);
            field.setAccessible(true);
            if (field.getType().equals(String.class) && params instanceof String) {
                final Object value = field.get(obj);
                if (value != null && ((String) value).contains((String) params)) {
                    return true;
                }
            } else {
                if (params.equals(field.get(obj))) {
                    return true;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.debug("error in throughFilter {}", e.getMessage());
        }
        return false;
    }

    private static <T> boolean notThrough(final Class<?> objClass, final T obj, final Map.Entry<String, Object> i) {
        try {
            Field field = objClass.getDeclaredField(i.getKey());
            field.setAccessible(true);
            if (field.getType().equals(String.class) && i.getValue() instanceof String) {
                final Object value = field.get(obj);
                if (value == null || !((String) value).toLowerCase().contains((String) i.getValue())) {
                    return true;
                }
            } else {
                if (i.getValue().equals(field.get(obj))) {
                    return true;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.debug("error in throughFilter {}", e.getMessage());
        }
        return false;
    }
}
