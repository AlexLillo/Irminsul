package com.alex.irminsul.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlatformRequestDTO {

    @NotBlank
    @Size(max = 50)
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