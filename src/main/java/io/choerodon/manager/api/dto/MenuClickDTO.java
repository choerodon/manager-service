package io.choerodon.manager.api.dto;

import java.util.List;

/**
 * @author superlee
 */
public class MenuClickDTO {

    private String level;

    private List<Menu> menus;

    public MenuClickDTO() {
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public static class Menu {

        private String code;

        private String name;

        private Integer count;

        public Menu() {
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

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "Menu{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MenuClickDTO{" +
                "level='" + level + '\'' +
                ", menus=" + menus +
                '}';
    }
}
