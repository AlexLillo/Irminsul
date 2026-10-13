package com.alex.irminsul.dto;

import com.alex.irminsul.entity.Genre;

import java.time.LocalDate;
import java.util.Set;

public class GameResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private String coverUrl;
    private Genre genre;
    private Set<PlatformResponseDTO> platforms;
    private Double averageRating;
    private Long ratingCount;

    public GameResponseDTO() {
    }

    public GameResponseDTO(Long id,
                           String title,
                           String description,
                           LocalDate releaseDate,
                           String coverUrl,
                           Genre genre,
                           Set<PlatformResponseDTO> platforms) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
        this.genre = genre;
        this.platforms = platforms;
        this.averageRating = null;
        this.ratingCount = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlatforms(Set<PlatformResponseDTO> platforms) {
        this.platforms = platforms;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<PlatformResponseDTO> getPlatforms() {
        return platforms;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }
}
