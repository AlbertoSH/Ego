package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoObject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ForTesting extends EgoObject {

    private LocalDate localDate;
    private Duration duration;
    private LocalDateTime localDateTime;

    public LocalDate getLocalDate() {
        return localDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
