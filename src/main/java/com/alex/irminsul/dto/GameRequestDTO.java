package com.alex.irminsul.dto;

import com.alex.irminsul.entity.Genre;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

public class GameRequestDTO {

    @NotBlank
    private String title;

    private String description;

    private LocalDate releaseDate;

    private String coverUrl;

    private Genre genre;

    private Set<Long> platformIds;

    public GameRequestDTO() {
    }

    public GameRequestDTO(String title,
                          String description,
                          LocalDate releaseDate,
                          String coverUrl,
                          Genre genre,
                          Set<Long> platformIds) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
        this.genre = genre;
        this.platformIds = platformIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Long> getPlatformIds() {
        return platformIds;
    }

    public void setPlatformIds(Set<Long> platformIds) {
        this.platformIds = platformIds;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}