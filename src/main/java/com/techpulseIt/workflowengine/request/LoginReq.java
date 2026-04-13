package com.techpulseIt.workflowengine.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginReq (@NotBlank String email,
        @NotBlank String password){
}

