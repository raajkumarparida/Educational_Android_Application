package com.sansan.vikashtutorial.Item;

public class FacultyItem {

    private String name;
    private String sub;
    private String qua;
    private String img;

    public FacultyItem() {
    }

    public FacultyItem(String name, String sub, String qua, String img) {
        this.name = name;
        this.sub = sub;
        this.qua = qua;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getQua() {
        return qua;
    }

    public void setQua(String qua) {
        this.qua = qua;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
