package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoObject;

import java.time.LocalDate;

public class ForTesting extends EgoObject {

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public ForTesting setDate(LocalDate date) {
        this.date = date;
        return this;
    }
}
