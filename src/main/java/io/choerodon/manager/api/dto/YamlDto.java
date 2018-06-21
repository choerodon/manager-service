package io.choerodon.manager.api.dto;

import java.util.ArrayList;
import java.util.List;

public class YamlDto {

    private String yaml;

    private List<HighlightMarker> highlightMarkers = new ArrayList<>();

    private int totalLine;

    private Long objectVersionNumber;

    public YamlDto() {
    }

    public YamlDto(String yaml, int totalLine) {
        this.yaml = yaml;
        this.totalLine = totalLine;
    }

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }

    public List<HighlightMarker> getHighlightMarkers() {
        return highlightMarkers;
    }

    public void setHighlightMarkers(List<HighlightMarker> highlightMarkers) {
        this.highlightMarkers = highlightMarkers;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    static class HighlightMarker {

        private int line;
        private int startIndex;
        private int endIndex;
        private int startColumn;
        private int endColumn;

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        public int getStartColumn() {
            return startColumn;
        }

        public void setStartColumn(int startColumn) {
            this.startColumn = startColumn;
        }

        public int getEndColumn() {
            return endColumn;
        }

        public void setEndColumn(int endColumn) {
            this.endColumn = endColumn;
        }
    }
}
