package org.diceresearch.webservices.model.dto;

import java.io.Serializable;
import java.util.List;

public class DataSetLongViewDTO implements Serializable {
    private static final long serialVersionUID = 7992064242653372582L;
    private String uri;
    private String title;
    private String description;
    private String issueDate;
    private String theme;
    private List<String> keywords;
    private String fileType;
    private String overallRating;
    private String catalog;

    public DataSetLongViewDTO() {
    }

    public DataSetLongViewDTO(String uri, String title, String description, String issueDate, String theme, List<String> keywords, String fileType, String overallRating, String catalog) {
        this.uri = uri;
        this.title = title;
        this.description = description;
        this.issueDate = issueDate;
        this.theme = theme;
        this.keywords = keywords;
        this.fileType = fileType;
        this.overallRating = overallRating;
        this.catalog = catalog;
    }

    public String getTitle() {
        return title;
    }

    public DataSetLongViewDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DataSetLongViewDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public DataSetLongViewDTO setIssueDate(String issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public DataSetLongViewDTO setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public DataSetLongViewDTO setKeywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public DataSetLongViewDTO setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public DataSetLongViewDTO setOverallRating(String overallRating) {
        this.overallRating = overallRating;
        return this;
    }

    public String getCatalog() {
        return catalog;
    }

    public DataSetLongViewDTO setCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public DataSetLongViewDTO setUri(String uri) {
        this.uri = uri;
        return this;
    }
}
