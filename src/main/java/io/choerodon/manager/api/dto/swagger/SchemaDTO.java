package io.choerodon.manager.api.dto.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author superlee
 */
public class SchemaDTO {
    private String type;
    private Map<String, String> items;
    @JsonProperty("$ref")
    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}
