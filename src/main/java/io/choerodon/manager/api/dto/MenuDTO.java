package io.choerodon.manager.api.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import java.util.Objects;

/**
 * @author superlee
 * @since 2019-04-15
 */
public class MenuDTO extends BaseDTO {

    private Long id;
    private String code;
    private String name;
    private String pagePermissionCode;
    private String parentCode;
    private String resourceLevel;
    private String type;
    private String serviceCode;
    private Integer sort;
    private Boolean isDefault;
    private String icon;
    private String category;
    private String searchCondition;
    private String route;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPagePermissionCode() {
        return pagePermissionCode;
    }

    public void setPagePermissionCode(String pagePermissionCode) {
        this.pagePermissionCode = pagePermissionCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getResourceLevel() {
        return resourceLevel;
    }

    public void setResourceLevel(String resourceLevel) {
        this.resourceLevel = resourceLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }


    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "MenuDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", pagePermissionCode='" + pagePermissionCode + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", resourceLevel='" + resourceLevel + '\'' +
                ", type='" + type + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", sort=" + sort +
                ", isDefault=" + isDefault +
                ", icon='" + icon + '\'' +
                ", category='" + category + '\'' +
                ", searchCondition='" + searchCondition + '\'' +
                ", route='" + route + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuDTO menuDTO = (MenuDTO) o;
        return Objects.equals(id, menuDTO.id) &&
                Objects.equals(code, menuDTO.code) &&
                Objects.equals(name, menuDTO.name) &&
                Objects.equals(pagePermissionCode, menuDTO.pagePermissionCode) &&
                Objects.equals(parentCode, menuDTO.parentCode) &&
                Objects.equals(resourceLevel, menuDTO.resourceLevel) &&
                Objects.equals(type, menuDTO.type) &&
                Objects.equals(serviceCode, menuDTO.serviceCode) &&
                Objects.equals(sort, menuDTO.sort) &&
                Objects.equals(isDefault, menuDTO.isDefault) &&
                Objects.equals(icon, menuDTO.icon) &&
                Objects.equals(category, menuDTO.category) &&
                Objects.equals(searchCondition, menuDTO.searchCondition) &&
                Objects.equals(route, menuDTO.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, pagePermissionCode, parentCode, resourceLevel, type, serviceCode, sort, isDefault, icon, category, searchCondition, route);
    }
}
