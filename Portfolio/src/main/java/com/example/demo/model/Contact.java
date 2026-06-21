package com.example.demo.model;

public class Contact {

    private String id;
    private String category;
    private String content;
    private String status;

    public Contact(
            String id,
            String category,
            String content,
            String status) {

        this.id = id;
        this.category = category;
        this.content = content;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {

        if (content.length() > 10) {

            return content.substring(0, 10);
        }

        return content;
    }
}
