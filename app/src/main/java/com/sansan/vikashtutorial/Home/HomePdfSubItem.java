package com.sansan.vikashtutorial.Home;

import android.os.Parcel;
import android.os.Parcelable;

public class HomePdfSubItem implements Parcelable {
    private String subItemImage;
    private String subItemTitle;
    private String subItemDesc;
    private String subItemUrl;

    public HomePdfSubItem() {
    }

    public HomePdfSubItem(String subItemImage, String subItemTitle, String subItemDesc, String subItemUrl) {
        this.subItemImage = subItemImage;
        this.subItemTitle = subItemTitle;
        this.subItemDesc = subItemDesc;
        this.subItemUrl = subItemUrl;
    }

    protected HomePdfSubItem(Parcel in) {
        subItemImage = in.readString();
        subItemTitle = in.readString();
        subItemDesc = in.readString();
        subItemUrl = in.readString();
    }

    public static final Parcelable.Creator<HomePdfSubItem> CREATOR = new Parcelable.Creator<HomePdfSubItem>() {
        @Override
        public HomePdfSubItem createFromParcel(Parcel in) {
            return new HomePdfSubItem(in);
        }

        @Override
        public HomePdfSubItem[] newArray(int size) {
            return new HomePdfSubItem[size];
        }
    };

    public String getSubItemImage() {
        return subItemImage;
    }

    public void setSubItemImage(String subItemImage) {
        this.subItemImage = subItemImage;
    }

    public String getSubItemTitle() {
        return subItemTitle;
    }

    public void setSubItemTitle(String subItemTitle) {
        this.subItemTitle = subItemTitle;
    }

    public String getSubItemDesc() {
        return subItemDesc;
    }

    public void setSubItemDesc(String subItemDesc) {
        this.subItemDesc = subItemDesc;
    }

    public String getSubItemUrl() {
        return subItemUrl;
    }

    public void setSubItemUrl(String subItemUrl) {
        this.subItemUrl = subItemUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(subItemImage);
        parcel.writeString(subItemTitle);
        parcel.writeString(subItemDesc);
        parcel.writeString(subItemUrl);
    }
}
