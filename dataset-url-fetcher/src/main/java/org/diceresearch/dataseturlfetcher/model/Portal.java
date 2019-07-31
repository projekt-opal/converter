package org.diceresearch.dataseturlfetcher.model;

import javax.persistence.*;

@Entity
public class Portal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private int lastNotFetched;

    @Column
    private int high;

    public String getName() {
        return name;
    }

    public Portal setName(String name) {
        this.name = name;
        return this;
    }

    public int getLastNotFetched() {
        return lastNotFetched;
    }

    public Portal setLastNotFetched(int lastNotFetched) {
        this.lastNotFetched = lastNotFetched;
        return this;
    }

    public int getHigh() {
        return high;
    }

    public Portal setHigh(int high) {
        this.high = high;
        return this;
    }

    public int getId() {
        return id;
    }

    public Portal setId(int id) {
        this.id = id;
        return this;
    }
}
