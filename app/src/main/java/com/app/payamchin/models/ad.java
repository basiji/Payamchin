package com.app.payamchin.models;

public class ad {

    private String number, keyword;
    private int times;

    public ad(String number, String keyword, int times){
        this.keyword = keyword;
        this.number = number;
        this.times = times;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


}
