package org.diceresearch.elasticsearchwriter.entity;

import lombok.Data;

@Data
public class Publisher {
    private String name;
    private String uri;
    private String website;
    private String email;
}
