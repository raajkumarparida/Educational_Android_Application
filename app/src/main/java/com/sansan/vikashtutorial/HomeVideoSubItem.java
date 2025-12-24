package com.sansan.vikashtutorial;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeVideoSubItem implements Parcelable {
    private String videoThumbnail;
    private String videoTitle;
    private String videoDescription;
    private String videoLink;
    private String videoLength;

    public HomeVideoSubItem() {
    }

    public HomeVideoSubItem(String videoThumbnail, String videoTitle, String videoDescription, String videoLink, String videoLength) {
        this.videoThumbnail = videoThumbnail;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoLink = videoLink;
        this.videoLength = videoLength;
    }

    protected HomeVideoSubItem(Parcel in) {
        videoThumbnail = in.readString();
        videoTitle = in.readString();
        videoDescription = in.readString();
        videoLink = in.readString();
        videoLength = in.readString();
    }

    public static final Parcelable.Creator<HomeVideoSubItem> CREATOR = new Parcelable.Creator<HomeVideoSubItem>() {
        @Override
        public HomeVideoSubItem createFromParcel(Parcel in) {
            return new HomeVideoSubItem(in);
        }

        @Override
        public HomeVideoSubItem[] newArray(int size) {
            return new HomeVideoSubItem[size];
        }
    };

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(videoThumbnail);
        parcel.writeString(videoTitle);
        parcel.writeString(videoDescription);
        parcel.writeString(videoLink);
        parcel.writeString(videoLength);
    }
}
