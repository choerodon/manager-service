package io.choerodon.manager.api.dto.swagger;

/**
 * @author superlee
 */
public class ParameterDTO {

    private String description;
    private String in;
    private String name;
    private Boolean required;
    private String type;
    private String format;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterDTO that = (ParameterDTO) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (in != null ? !in.equals(that.in) : that.in != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (required != null ? !required.equals(that.required) : that.required != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return format != null ? format.equals(that.format) : that.format == null;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (in != null ? in.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        return result;
    }
}
