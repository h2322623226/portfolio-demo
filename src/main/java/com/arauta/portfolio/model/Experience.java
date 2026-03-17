package com.arauta.portfolio.model;

import jakarta.persistence.*;


@Entity
@Table(name = "experience_item")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String year;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    protected Experience() {}

    public Experience(String year, String title, String body, int sortOrder) {
        this.year = year;
        this.title = title;
        this.body = body;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
