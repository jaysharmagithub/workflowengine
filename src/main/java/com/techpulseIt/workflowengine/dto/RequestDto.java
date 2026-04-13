package com.techpulseIt.workflowengine.dto;

import lombok.Builder;

@Builder
public record RequestDto(
        String type,
        String status,
        String createdBy,
        String createdAt
) {
}
