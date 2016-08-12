package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoObject;

import java.time.LocalDate;

public class ItemWithLocalDate extends EgoObject {

    private LocalDate aDate;

    public LocalDate getADate() {
        return aDate;
    }


    private String str;

    public String getStr() {
        return str;
    }
}
