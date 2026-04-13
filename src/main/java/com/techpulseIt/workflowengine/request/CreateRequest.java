package com.techpulseIt.workflowengine.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateRequest(
        @NotBlank
        String type,
        @NotBlank
        String username
) {
}
