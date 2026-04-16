package com.arauta.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ExperienceForm {

    @NotBlank(message = "年份不可為空")
    @Size(max = 4, message = "年份不可超過 4 字元")
    private String year;

    @NotBlank(message = "標題不可為空")
    @Size(max = 200, message = "標題不可超過 200 字元")
    private String title;

    private String body;

    public ExperienceForm() { }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
