package com.sansan.vikashtutorial.Pdf;

public class PdfBookmarkItem {

    private String bookedImg;
    private String bookedTopic;
    private String bookedUrl;

    public PdfBookmarkItem() {
    }

    public PdfBookmarkItem(String bookedImg, String bookedTopic, String bookedUrl) {
        this.bookedImg = bookedImg;
        this.bookedTopic = bookedTopic;
        this.bookedUrl = bookedUrl;
    }

    public String getBookedImg() {
        return bookedImg;
    }

    public void setBookedImg(String bookedImg) {
        this.bookedImg = bookedImg;
    }

    public String getBookedTopic() {
        return bookedTopic;
    }

    public void setBookedTopic(String bookedTopic) {
        this.bookedTopic = bookedTopic;
    }

    public String getBookedUrl() {
        return bookedUrl;
    }

    public void setBookedUrl(String bookedUrl) {
        this.bookedUrl = bookedUrl;
    }
}
