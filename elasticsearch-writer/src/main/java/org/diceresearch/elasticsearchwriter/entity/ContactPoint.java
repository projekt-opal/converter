package org.diceresearch.elasticsearchwriter.entity;

import lombok.Data;

@Data
public class ContactPoint {
    private String email;
    private String name;
    private String address;
    private String phone;
}
