package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.dto;
import java.time.Duration;
public record MovieCreateRequest(String title,  Duration duration, String PosterURL) {
    public String getTitle() {
        return title;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getPosterURL (){
        return PosterURL;
    }



}