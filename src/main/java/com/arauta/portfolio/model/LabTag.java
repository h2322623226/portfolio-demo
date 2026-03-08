package com.arauta.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lab_tag")
public class LabTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_item_id", nullable = false)
    private LabItem labItem;

    @Column(name = "tag_value", nullable = false, length = 50)
    private String tagValue;

    @Column(name = "tag_order", nullable = false)
    private int tagOrder = 0;

    protected LabTag() {}

    public LabTag(LabItem labItem, String tagValue, int tagOrder) {
        this.labItem = labItem;
        this.tagValue = tagValue;
        this.tagOrder = tagOrder;
    }

    public Long getId() { return id; }
    public LabItem getLabItem() { return labItem; }
    public String getTagValue() { return tagValue; }
    public void setTagValue(String v) { tagValue = v; }
    public int getTagOrder() { return tagOrder; }
    public void setTagOrder(int v) { tagOrder = v; }
}
