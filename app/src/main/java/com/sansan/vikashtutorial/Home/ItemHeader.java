package com.sansan.vikashtutorial.Home;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class ItemHeader<T extends Parcelable> implements Parcelable {
    private String itemTitle;
    private List<T> subItems;

    public ItemHeader() {
    }

    public ItemHeader(String itemTitle, List<T> subItems) {
        this.itemTitle = itemTitle;
        this.subItems = subItems;
    }

    protected ItemHeader(Parcel in) {
        itemTitle = in.readString();
        subItems = in.createTypedArrayList(null);
    }

    public static final Parcelable.Creator<ItemHeader> CREATOR = new Parcelable.Creator<ItemHeader>() {
        @Override
        public ItemHeader createFromParcel(Parcel in) {
            return new ItemHeader(in);
        }

        @Override
        public ItemHeader[] newArray(int size) {
            return new ItemHeader[size];
        }
    };

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public List<T> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<T> subItems) {
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
