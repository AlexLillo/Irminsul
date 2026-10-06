package com.alex.irminsul.dto;

import com.alex.irminsul.entity.Status;

public class UserGameResponseDTO {

    private Long gameId;
    private String gameTitle;
    private String gameCoverUrl;
    private Status status;
    private Integer rating;
    private String notes;

    public UserGameResponseDTO() {
    }

    public UserGameResponseDTO(Long gameId,
                               String gameTitle,
                               String gameCoverUrl,
                               Status status,
                               Integer rating,
                               String notes) {
        this.gameId = gameId;
        this.gameTitle = gameTitle;
        this.gameCoverUrl = gameCoverUrl;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGameCoverUrl() {
        return gameCoverUrl;
    }

    public void setGameCoverUrl(String gameCoverUrl) {
        this.gameCoverUrl = gameCoverUrl;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }
}