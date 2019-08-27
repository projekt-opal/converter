package org.diceresearch.datasetfetcher.model;

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

    @Column
    private String queryAddress;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String outputQueue;

    public String getQueryAddress() {
        return queryAddress;
    }

    public Portal setQueryAddress(String queryAddress) {
        this.queryAddress = queryAddress;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Portal setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Portal setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getOutputQueue() {
        return outputQueue;
    }

    public Portal setOutputQueue(String outputQueue) {
        this.outputQueue = outputQueue;
        return this;
    }

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
