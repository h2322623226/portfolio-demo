package com.example.securitydemo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * [動態內容項目實體]
 * 用於管理頁面中可增減的重複性區塊（如：作品集項目、技術卡片）。
 * 區別於 ContentBlock 的單一配置，本類別支援基於 ID 的 CRUD 運作。
 */
@Entity
@Table(name = "project_item")
public class ProjectItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_name", nullable = false, length = 50)
    private String pageName;

    @Column(name = "item_key", nullable = false, length = 50)
    private String key;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @OneToMany(mappedBy = "projectItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tagOrder ASC")
    private List<ProjectTag> tags = new ArrayList<>();

    public List<ProjectTag> getTags() { return tags; }

    protected ProjectItem() { }


    public ProjectItem(String pageName, String key, String title, String content, String imageUrl) {
        this.pageName = pageName;
        this.key = key;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getPageName() { return pageName; }
    public String getKey() { return key; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
