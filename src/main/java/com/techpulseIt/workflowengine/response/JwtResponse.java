package com.techpulseIt.workflowengine.response;

import lombok.Builder;

import java.util.List;

@Builder
public record JwtResponse(Long id, String email, String jwt, List<String> roles) {
}
