package org.diceresearch.webservices.model.dto;

import java.io.Serializable;

public class FilterValueDTO implements Serializable {
    private static final long serialVersionUID = -3429602967985658169L;
    private String uri;
    private String value;
    private Integer count;

    public FilterValueDTO() {
    }

    public FilterValueDTO(String uri, String value, Integer count) {
        this.uri = uri;
        this.value = value;
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public FilterValueDTO setValue(String value) {
        this.value = value;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public FilterValueDTO setCount(Integer count) {
        this.count = count;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public FilterValueDTO setUri(String uri) {
        this.uri = uri;
        return this;
    }
}
