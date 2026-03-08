package com.arauta.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_tag")
public class ProjectTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_item_id")
    private ProjectItem projectItem;

    @Column(name = "tag_value", nullable = false, length = 50)
    private String tagValue;

    @Column(name = "tag_order", nullable = false)
    private int tagOrder = 0;

    protected ProjectTag() {}

    public ProjectTag(ProjectItem projectItem, String tagValue, int tagOrder) {
        this.projectItem = projectItem;
        this.tagValue = tagValue;
        this.tagOrder = tagOrder;
    }

    public Long getId() { return id; }
    public ProjectItem getProjectItem() { return projectItem; }
    public void setProjectItem(ProjectItem projectItem) { this.projectItem = projectItem; }
    public String getTagValue() { return tagValue; }
    public void setTagValue(String tagValue) { this.tagValue = tagValue; }
    public int getTagOrder() { return tagOrder; }
    public void setTagOrder(int tagOrder) { this.tagOrder = tagOrder; }
}