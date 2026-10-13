package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.CreateLeaveRequest;
import com.example.leavemanagement.dto.LeaveResponse;
import com.example.leavemanagement.exception.LeaveNotFoundException;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.repository.LeaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {
    @Mock private LeaveRepository repository;
    @InjectMocks private LeaveServiceImpl service;

    @Test void createLeaveSetsPendingStatusAndReturnsSavedRequest() {
        when(repository.save(any(LeaveRequest.class))).thenAnswer(invocation -> {
            LeaveRequest leave = invocation.getArgument(0);
            leave.setId(1L);
            return leave;
        });
        CreateLeaveRequest request = CreateLeaveRequest.builder().employeeId("EMP-1")
                .leaveType("ANNUAL").startDate(LocalDate.parse("2026-11-01"))
                .endDate(LocalDate.parse("2026-11-03")).reason("Family trip").build();

        LeaveResponse response = service.createLeave(request);

        assertEquals(1L, response.getId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("EMP-1", response.getEmployeeId());
        verify(repository).save(any(LeaveRequest.class));
    }

    @Test void getLeaveByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(LeaveNotFoundException.class, () -> service.getLeaveById(99L));
    }

    @Test void createLeaveRejectsEndDateBeforeStartDate() {
        CreateLeaveRequest request = CreateLeaveRequest.builder().employeeId("EMP-1")
                .leaveType("ANNUAL").startDate(LocalDate.parse("2026-11-04"))
                .endDate(LocalDate.parse("2026-11-03")).reason("Trip").build();
        assertThrows(IllegalArgumentException.class, () -> service.createLeave(request));
        verify(repository, never()).save(any());
    }
}
