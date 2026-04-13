package com.techpulseIt.workflowengine.response;


import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Builder
public record SuccessResponse<T>(
        int status,
        String message,
        T data,
        String timeStamp,
        String path


) {
    public static <T> ResponseEntity<SuccessResponse<T>> ok(T data, String message, String path) {
        return ResponseEntity.ok(
                SuccessResponse.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .timeStamp(Instant.now().toString())
                        .path(path)
                        .build()
        );

    }

//    public static <T> ResponseEntity<SuccessResponse<T>> ok(String message, String path) {
//        return ResponseEntity.ok(
//                SuccessResponse.<T>builder()
//                        .status(HttpStatus.OK.value())
//                        .message(message)
//                        .timeStamp(Instant.now().toString())
//                        .path(path)
//                        .build()
//        );
//
//    }

    public static <T> ResponseEntity<SuccessResponse<T>> created(T data, String message, String path) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.<T>builder()
                        .status(HttpStatus.CREATED.value())
                        .message(message)
                        .data(data)
                        .timeStamp(Instant.now().toString())
                        .path(path)
                        .build()
        );
    }
}
