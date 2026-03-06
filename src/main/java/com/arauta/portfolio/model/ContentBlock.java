package com.arauta.portfolio.model;

import jakarta.persistence.*;


@Entity
@Table(
    name = "content_block",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"page_name", "content_key"})
    }
)
public class ContentBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_name", nullable = false, length = 50)
    private String pageName;

    @Column(name = "content_key", nullable = false, length = 50)
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    protected ContentBlock() { }

    public ContentBlock(String pageName, String key, String content) {
        this.pageName = pageName;
        this.key = key;
        this.content = content;
    }

    public Long getId() { return id; }
    public String getPageName() { return pageName; }
    public String getKey() { return key; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
