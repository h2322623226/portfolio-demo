package com.arauta.portfolio.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public class SectionForm {

    @Size(max = 100, message = "區塊標籤不可超過 100 字元")
    private String sectionLabel;

    @Size(max = 50, message = "群組識別碼不可超過 50 字元")
    private String groupKey;

    @Size(max = 200, message = "標題不可超過 200 字元")
    private String title;

    private String subtitle;
    private String body;
    private List<String> tags;

    public SectionForm() { }

    public String getSectionLabel() { return sectionLabel; }
    public void setSectionLabel(String sectionLabel) { this.sectionLabel = sectionLabel; }

    public String getGroupKey() { return groupKey; }
    public void setGroupKey(String groupKey) { this.groupKey = groupKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
