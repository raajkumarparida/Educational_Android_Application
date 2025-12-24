package com.sansan.vikashtutorial.Pdf;

public class PdfItem {

    private String img;
    private String topic;
    private String subject;
    private String url;

    public PdfItem() {
    }

    public PdfItem(String img, String topic, String subject, String url) {
        this.img = img;
        this.topic = topic;
        this.subject = subject;
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
