package com.alex.irminsul.dto;

import jakarta.validation.constraints.NotBlank;

public class PlatformRequestDTO {

    @NotBlank
    private String name;

    public PlatformRequestDTO() {
    }

    public PlatformRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}