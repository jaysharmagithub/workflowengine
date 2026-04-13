package com.techpulseIt.workflowengine.service;

import com.techpulseIt.workflowengine.dto.RequestDto;
import com.techpulseIt.workflowengine.entity.ApprovalHistory;
import com.techpulseIt.workflowengine.entity.ApprovalStep;
import com.techpulseIt.workflowengine.entity.Request;
import com.techpulseIt.workflowengine.repository.ApprovalHistoryRepository;
import com.techpulseIt.workflowengine.repository.ApprovalStepRepository;
import com.techpulseIt.workflowengine.repository.RequestRepository;
import com.techpulseIt.workflowengine.request.CreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final ApprovalStepRepository stepRepository;
    private final ApprovalHistoryRepository historyRepository;

    @Transactional
    public RequestDto createRequest(CreateRequest createRequest) {
        Request request = new Request();
        request.setType(createRequest.type());
        request.setStatus("PENDING");
        request.setCreatedBy(createRequest.username());
        request.setCreatedAt(LocalDateTime.now());
        request.setCurrentStepOrder(1);

        Request r = requestRepository.save(request);
        return RequestDto
                .builder()
                .type(r.getType())
                .status(r.getStatus())
                .createdBy(r.getCreatedBy())
                .createdAt(r.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    public RequestDto getRequest(Long id) {
        return requestRepository
                .findById(id)
                .map(request ->
                        RequestDto.builder().type(request.getType()).status(request.getStatus()).build()
                )
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }



    @Transactional
    public void approve(Long requestId, Long userId, String role) {

        Request request = requestRepository
                .findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStatus().equals("PENDING")) {
            throw new RuntimeException("Request already processed");

        }

        if (request.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Requester cannot approve own request");
        }

        ApprovalStep step = stepRepository
                .findByRequestTypeAndStepOrder(request.getType(), request.getCurrentStepOrder())
                .orElseThrow(() -> new RuntimeException("Step not found"));

        if (!step.getRole().equals(role)) {
            throw new RuntimeException("Unauthorized for this step");
        }

        // save history
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(request);
        history.setAction("APPROVED");
        history.setActionBy(userId);
        history.setActionAt(LocalDateTime.now());
        historyRepository.save(history);

        // next step
        int nextStep = request.getCurrentStepOrder() + 1;

        boolean hasNext = stepRepository
                .findByRequestTypeAndStepOrder(request.getType(), nextStep)
                .isPresent();

        if (hasNext) {
            request.setCurrentStepOrder(nextStep);
        } else {
            request.setStatus("APPROVED");
        }

        requestRepository.save(request);
    }

    // ✅ REJECT
    @Transactional
    public void reject(Long requestId, Long userId, String role) {

        Request request = requestRepository
                .findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStatus().equals("PENDING")) {
            throw new RuntimeException("Request already processed");
        }

        ApprovalStep step = stepRepository
                .findByRequestTypeAndStepOrder(request.getType(), request.getCurrentStepOrder())
                .orElseThrow(() -> new RuntimeException("Step not found"));

        if (!step.getRole().equals(role)) {
            throw new RuntimeException("Unauthorized for this step");
        }

        // save history
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(request);
        history.setAction("REJECTED");
        history.setActionBy(userId);
        history.setActionAt(LocalDateTime.now());
        historyRepository.save(history);

        request.setStatus("REJECTED");
        requestRepository.save(request);
    }

    // ✅ HISTORY
    public List<ApprovalHistory> getHistory(Long requestId) {
        return historyRepository.findByRequestIdOrderByActionAtAsc(requestId);
    }
}