package org.diceresearch.webservices.model.dto;

import java.io.Serializable;

public class ReceivingFilterDTO implements Serializable {
    private static final long serialVersionUID = 3831420667948753173L;

    private String property;
    private String[] values;

    public ReceivingFilterDTO() {
    }

    public ReceivingFilterDTO(String property, String[] values) {
        this.property = property;
        this.values = values;
    }

    public String getProperty() {
        return property;
    }

    public ReceivingFilterDTO setProperty(String property) {
        this.property = property;
        return this;
    }

    public String[] getValues() {
        return values;
    }

    public ReceivingFilterDTO setValues(String[] values) {
        this.values = values;
        return this;
    }
}
