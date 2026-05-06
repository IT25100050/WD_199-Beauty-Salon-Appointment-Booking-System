package com.beauty_salon.beautysalon.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 500)
    private String review;
}
