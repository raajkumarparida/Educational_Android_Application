package com.sansan.vikashtutorial.Home;

public class HomeContentItem {
    private String subItemImage;
    private String subItemTitle;
    private String subItemDesc;
    private String activityCode;

    public HomeContentItem() {
    }

    public HomeContentItem(String subItemImage, String subItemTitle, String subItemDesc, String activityCode) {
        this.subItemImage = subItemImage;
        this.subItemTitle = subItemTitle;
        this.subItemDesc = subItemDesc;
        this.activityCode = activityCode;
    }

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

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
}
