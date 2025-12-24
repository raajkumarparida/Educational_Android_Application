package com.sansan.vikashtutorial.Item;

import java.util.List;

public class DropDownItem {

    private List<String> subjectNames;
    private List<String> subjectUrls;
    private String examTitle;
    private String examImg;
    private boolean isExpandable;

    public DropDownItem() {
    }

    public DropDownItem(List<String> subjectNames, List<String> subjectUrls, String semesterName, String examImg, boolean isExpandable) {
        this.subjectNames = subjectNames;
        this.subjectUrls = subjectUrls;
        this.examTitle = semesterName;
        this.examImg = examImg;
        this.isExpandable = isExpandable;
    }


    public List<String> getSubjectNames() {
        return subjectNames;
    }

    public void setSubjectNames(List<String> subjectNames) {
        this.subjectNames = subjectNames;
    }

    public List<String> getSubjectUrls() {
        return subjectUrls;
    }

    public void setSubjectUrls(List<String> subjectUrls) {
        this.subjectUrls = subjectUrls;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getExamImg() {
        return examImg;
    }

    public void setExamImg(String examImg) {
        this.examImg = examImg;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
}
