package com.sansan.vikashtutorial.Video;

public class VideoItem {

    private String title;
    private String description;
    private String thumbnail;
    private String link;
    private String length;

    public VideoItem() {
    }

    public VideoItem(String title, String description, String thumbnail, String link, String length) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.link = link;
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getFormattedLength() {
        try {
            int len = Integer.parseInt(length);
            int minutes = len / 60;
            int seconds = len % 60;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }
}
