package com.arauta.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "section_tag")
public class SectionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_item_id")
    private SectionItem sectionItem;

    @Column(name = "tag_value", nullable = false, length = 50)
    private String tagValue;

    @Column(name = "tag_order", nullable = false)
    private int tagOrder = 0;

    protected SectionTag() {}

    public SectionTag(SectionItem item, String tagValue, int tagOrder) {
        this.sectionItem = item;
        this.tagValue = tagValue;
        this.tagOrder = tagOrder;
    }

    public Long getId() { return id; }
    public SectionItem getSectionItem() { return sectionItem; }
    public void setSectionItem(SectionItem sectionItem) { this.sectionItem = sectionItem; }
    public String getTagValue() { return tagValue; }
    public void setTagValue(String tagValue) { this.tagValue = tagValue; }
    public int getTagOrder() { return tagOrder; }
    public void setTagOrder(int tagOrder) { this.tagOrder = tagOrder; }
}