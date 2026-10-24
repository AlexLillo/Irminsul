package com.alex.irminsul.dto;

import com.alex.irminsul.entity.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserGameRequestDTO {

    @NotNull
    private Long gameId;

    @NotNull
    private Status status;

    @Min(1)
    @Max(10)
    private Integer rating;

    @Size(max = 1000)
    private String notes;

    public UserGameRequestDTO() {
    }

    public UserGameRequestDTO(Long gameId,
                              Status status,
                              Integer rating,
                              String notes) {
        this.gameId = gameId;
        this.status = status;
        this.rating = rating;
        this.notes = notes;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}