package com.sansan.vikashtutorial;

import android.os.Parcel;
import android.os.Parcelable;

import com.sansan.vikashtutorial.Mock.MockItem;

import java.util.List;

public class MockHeader implements Parcelable {
    private String itemTitle;
    private List<MockItem> subItems;

    public MockHeader() {
    }

    public MockHeader(String itemTitle, List<MockItem> subItems) {
        this.itemTitle = itemTitle;
        this.subItems = subItems;
    }

    protected MockHeader(Parcel in) {
        itemTitle = in.readString();
        subItems = in.createTypedArrayList(MockItem.CREATOR);
    }

    public static final Creator<MockHeader> CREATOR = new Creator<MockHeader>() {
        @Override
        public MockHeader createFromParcel(Parcel in) {
            return new MockHeader(in);
        }

        @Override
        public MockHeader[] newArray(int size) {
            return new MockHeader[size];
        }
    };

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public List<MockItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<MockItem> subItems) {
        this.subItems = subItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(itemTitle);
        parcel.writeTypedList(subItems);
    }
}
