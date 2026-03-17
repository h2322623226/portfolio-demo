package com.arauta.portfolio.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "section_item")
public class Section {

    public enum GroupType {
        SINGLE,    // 全寬單卡（ABOUT 樣式）
        TWO_COL,   // 兩欄並排（PROJECTS 樣式）
        TWO_ONE,   // 兩小一大（SKILLS 樣式）
        SKILL_ROW  // 技能橫列（SKILLS 樣式，左標題右描述+tags）
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

    /** 卡片小標題 */
    @Column(name = "title", length = 200)
    private String title;

    /** TWO_COL 的 meta 說明 / WRITING 的日期分類 */
    @Column(name = "subtitle", length = 200)
    private String subtitle;

    /** 卡片主要內文 */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @OneToMany(mappedBy = "sectionItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tagOrder ASC")
    private List<SectionTag> tags = new ArrayList<>();

    protected Section() {}

    public Section(String pageName, String groupKey, GroupType groupType, int sortOrder) {
        this.pageName = pageName;
        this.groupKey = groupKey;
        this.groupType = groupType;
        this.sortOrder = sortOrder;
    }
    public String getBodyHtml() { return com.arauta.portfolio.util.HtmlUtils.nl2br(this.body); }
    public Long getId() { return id; }
    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }
    public String getGroupKey() { return groupKey; }
    public void setGroupKey(String groupKey) { this.groupKey = groupKey; }
    public GroupType getGroupType() { return groupType; }
    public void setGroupType(GroupType groupType) { this.groupType = groupType; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getSectionLabel() { return sectionLabel; }
    public void setSectionLabel(String sectionLabel) { this.sectionLabel = sectionLabel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public List<SectionTag> getTags() { return tags; }
}
