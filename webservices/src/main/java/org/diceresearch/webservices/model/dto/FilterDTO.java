package org.diceresearch.webservices.model.dto;

import java.io.Serializable;
import java.util.List;

public class FilterDTO implements Serializable {
    private static final long serialVersionUID = -7109984421794445640L;
    private String uri;
    private String title;
    private List<FilterValueDTO> values;

    public FilterDTO() {
    }

    public FilterDTO(String uri, String title, List<FilterValueDTO> values) {
        this.uri = uri;
        this.title = title;
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public FilterDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<FilterValueDTO> getValues() {
        return values;
    }

    public FilterDTO setValues(List<FilterValueDTO> values) {
        this.values = values;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public FilterDTO setUri(String uri) {
        this.uri = uri;
        return this;
    }
}
