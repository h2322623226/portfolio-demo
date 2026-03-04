package com.example.securitydemo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "section_item")
public class SectionItem {

    public enum GroupType {
        SINGLE,   // 全寬單卡（ABOUT 樣式）
        TWO_COL,  // 兩欄並排（PROJECTS 樣式）
        TWO_ONE,  // 兩小一大（SKILLS 樣式）
        TIMELINE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_name", nullable = false, length = 50)
    private String pageName;

    /** group 識別碼，同 key 的卡片會被並排在同一個 section */
    @Column(name = "group_key", nullable = false, length = 50)
    private String groupKey;

    /** 版型，同 group 共用，只需第一筆設定 */
    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false, length = 20)
    private GroupType groupType;

    /** 排序：group 間的順序 + group 內卡片的順序 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    /** 大區塊標籤（如 ABOUT / PROJECTS），只有 group 第一筆填 */
    @Column(name = "section_label", length = 100)
    private String sectionLabel;

    /** 卡片小標題 / TIMELINE 職稱 */
    @Column(name = "title", length = 200)
    private String title;

    /** TWO_COL 的 meta 說明 / WRITING 的日期分類 */
    @Column(name = "subtitle", length = 200)
    private String subtitle;

    /** TIMELINE 年份 */
    @Column(name = "year", length = 10)
    private String year;

    /** 卡片主要內文 */
    @Lob
    @Column(name = "body", length = 1000)
    private String body;

    @OneToMany(mappedBy = "sectionItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tagOrder ASC")
    private List<SectionTag> tags = new ArrayList<>();

    protected SectionItem() {}

    public SectionItem(String pageName, String groupKey, GroupType groupType, int sortOrder) {
        this.pageName = pageName;
        this.groupKey = groupKey;
        this.groupType = groupType;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public String getPageName() { return pageName; }
    public void setPageName(String v) { pageName = v; }
    public String getGroupKey() { return groupKey; }
    public void setGroupKey(String v) { groupKey = v; }
    public GroupType getGroupType() { return groupType; }
    public void setGroupType(GroupType v) { groupType = v; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int v) { sortOrder = v; }
    public String getSectionLabel() { return sectionLabel; }
    public void setSectionLabel(String v) { sectionLabel = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String v) { subtitle = v; }
    public String getYear() { return year; }
    public void setYear(String v) { year = v; }
    public String getBody() { return body; }
    public void setBody(String v) { body = v; }
    public List<SectionTag> getTags() { return tags; }
}