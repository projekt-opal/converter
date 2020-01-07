package org.diceresearch.datasetfetcher.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Portal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int lastNotFetched;
    private int high;
    private String queryAddress;
    private String username;
    @ToString.Exclude
    private String password;
    private String outputQueue;
    private Integer step;
    @Enumerated(EnumType.STRING)
    private WorkingStatus workingStatus;
}

