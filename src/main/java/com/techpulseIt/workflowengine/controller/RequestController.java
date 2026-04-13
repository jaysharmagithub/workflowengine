package com.techpulseIt.workflowengine.controller;

import com.techpulseIt.workflowengine.dto.RequestDto;
import com.techpulseIt.workflowengine.entity.ApprovalHistory;
import com.techpulseIt.workflowengine.response.SuccessResponse;
import com.techpulseIt.workflowengine.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.techpulseIt.workflowengine.request.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;


    @PostMapping
    @PreAuthorize("hasRole('REQUESTER')")
    public ResponseEntity<SuccessResponse<RequestDto>> create(@RequestBody CreateRequest request,
                                                              HttpServletRequest httpServletRequest){
            return SuccessResponse
                    .created(requestService.createRequest(request),
                            "Request created successfully",
                            httpServletRequest.getRequestURI()
                    );
    }

    @PreAuthorize("hasAnyRole('REQUESTER','APPROVER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<RequestDto>> get(@PathVariable Long id,
                                                           HttpServletRequest request) {
        return SuccessResponse
                .ok(requestService.getRequest(id),"The request retrieved successfully", request.getRequestURI());
    }


    @PreAuthorize("hasAnyRole('APPROVER','ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<SuccessResponse<Object>> approve(@PathVariable Long id,
                                                         @RequestParam Long userId,
                                                         @RequestParam String role,
                                                           HttpServletRequest request) {
        requestService.approve(id, userId, role);
        return SuccessResponse
                .ok(Collections.EMPTY_LIST,
                        "User registered successfully",
                        request.getRequestURI()
                );
    }


    @PreAuthorize("hasAnyRole('APPROVER','ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<SuccessResponse<Object>> reject(@PathVariable Long id,
                                                          @RequestParam Long userId,
                                                          @RequestParam String role,
                                                          HttpServletRequest httpServletRequest) {
        requestService.reject(id, userId, role);
        return SuccessResponse
                .ok(Collections.EMPTY_LIST,
                        "User registered successfully",
                        httpServletRequest.getRequestURI()
                );
    }

     @PreAuthorize("hasAnyRole('REQUESTER','ADMIN')")
     @GetMapping("/history/{id}")
    public ResponseEntity<SuccessResponse<List<ApprovalHistory>>> history(@PathVariable Long id,
                                                                          HttpServletRequest request) {
        return SuccessResponse
                .ok(requestService.getHistory(id),
                        "User registered successfully",
                        request.getRequestURI()
                );
    }
}