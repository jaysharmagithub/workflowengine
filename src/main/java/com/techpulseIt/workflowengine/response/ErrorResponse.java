package com.techpulseIt.workflowengine.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp

) {}
