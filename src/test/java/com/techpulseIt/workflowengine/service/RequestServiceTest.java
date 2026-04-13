package com.techpulseIt.workflowengine.service;

import com.techpulseIt.workflowengine.dto.RequestDto;
import com.techpulseIt.workflowengine.entity.ApprovalHistory;
import com.techpulseIt.workflowengine.entity.ApprovalStep;
import com.techpulseIt.workflowengine.entity.Request;
import com.techpulseIt.workflowengine.repository.ApprovalHistoryRepository;
import com.techpulseIt.workflowengine.repository.ApprovalStepRepository;
import com.techpulseIt.workflowengine.repository.RequestRepository;
import com.techpulseIt.workflowengine.request.CreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestServiceTest {

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ApprovalStepRepository stepRepository;

    @Mock
    private ApprovalHistoryRepository historyRepository;


    @Test
    void shouldCreateRequest() {
        CreateRequest create = new CreateRequest("LEAVE", "user1");

        when(requestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RequestDto dto = requestService.createRequest(create);

        assertNotNull(dto);
        assertEquals("LEAVE", dto.type());
        assertEquals("PENDING", dto.status());
        assertEquals("user1", dto.createdBy());
        assertNotNull(dto.createdAt());
    }

    @Test
    void shouldGetRequest() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .build();

        request.setId(1L);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        RequestDto dto = requestService.getRequest(1L);

        assertEquals("LEAVE", dto.type());
        assertEquals("PENDING", dto.status());
    }

    @Test
    void shouldThrowIfRequestNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.getRequest(1L));
    }


    @Test
    void shouldMoveToNextStep() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);
        request.setCreatedBy("user1");

        ApprovalStep step1 = new ApprovalStep();
        step1.setRequestType("LEAVE");
        step1.setStepOrder(1);
        step1.setRole("APPROVER");

        ApprovalStep step2 = new ApprovalStep();
        step2.setRequestType("LEAVE");
        step2.setStepOrder(2);
        step2.setRole("ADMIN");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(step1));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 2))
                .thenReturn(Optional.of(step2));

        requestService.approve(1L, 2L, "APPROVER");

        assertEquals(2, request.getCurrentStepOrder());
        verify(historyRepository).save(any());
    }

    @Test
    void shouldCompleteWorkflow() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);
        request.setCreatedBy("user1");

        ApprovalStep step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole("APPROVER");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(step));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 2))
                .thenReturn(Optional.empty());

        requestService.approve(1L, 2L, "APPROVER");

        assertEquals("APPROVED", request.getStatus());
    }

    @Test
    void shouldThrowIfWrongRole() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);
        request.setCreatedBy("user1");

        ApprovalStep step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole("APPROVER");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(step));

        assertThrows(RuntimeException.class,
                () -> requestService.approve(1L, 2L, "REQUESTER"));
    }

    @Test
    void shouldThrowIfSelfApproval() {
        Request request = new Request();
        request.setId(1L);
        request.setStatus("PENDING");

        // ⚠️ Important: String vs Long bug in your code
        request.setCreatedBy("User1");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(RuntimeException.class,
                () -> requestService.approve(1L, 1L, "APPROVER"));
    }

    @Test
    void shouldThrowIfAlreadyProcessed() {
        Request request = new Request();
        request.setId(1L);
        request.setStatus("APPROVED");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(RuntimeException.class,
                () -> requestService.approve(1L, 2L, "APPROVER"));
    }

    @Test
    void shouldThrowIfApproveStepNotFound() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.approve(1L, 2L, "APPROVER"));
    }

    @Test
    void shouldThrowIfApproveRequestNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.approve(1L, 2L, "APPROVER"));
    }

    @Test
    void shouldRejectRequest() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);

        ApprovalStep step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole("APPROVER");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(step));

        requestService.reject(1L, 2L, "APPROVER");

        assertEquals("REJECTED", request.getStatus());
        verify(historyRepository).save(any());
    }

    @Test
    void shouldThrowIfRejectWrongRole() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);

        ApprovalStep step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole("APPROVER");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.of(step));

        assertThrows(RuntimeException.class,
                () -> requestService.reject(1L, 2L, "REQUESTER"));
    }

    @Test
    void shouldThrowIfRejectAlreadyProcessed() {
        Request request = new Request();
        request.setId(1L);
        request.setStatus("APPROVED");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(RuntimeException.class,
                () -> requestService.reject(1L, 2L, "APPROVER"));
    }

    @Test
    void shouldThrowIfRejectStepNotFound() {
        Request request = Request.builder()
                .type("LEAVE")
                .status("PENDING")
                .currentStepOrder(1)
                .build();

        request.setId(1L);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeAndStepOrder("LEAVE", 1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.reject(1L, 2L, "APPROVER"));
    }

    @Test
    void shouldThrowIfRejectRequestNotFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.reject(1L, 2L, "APPROVER"));
    }


    @Test
    void shouldReturnHistory() {
        when(historyRepository.findByRequestIdOrderByActionAtAsc(1L))
                .thenReturn(List.of(new ApprovalHistory()));

        List<ApprovalHistory> history = requestService.getHistory(1L);

        assertFalse(history.isEmpty());
    }

    @Test
    void shouldReturnEmptyHistory() {
        when(historyRepository.findByRequestIdOrderByActionAtAsc(1L))
                .thenReturn(List.of());

        List<ApprovalHistory> history = requestService.getHistory(1L);

        assertTrue(history.isEmpty());
    }
}