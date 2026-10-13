package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.*;
import com.example.leavemanagement.exception.LeaveNotFoundException;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRepository leaveRepository;

    @Override public List<LeaveResponse> getAllLeaves() {
        return leaveRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override public LeaveResponse getLeaveById(Long id) {
        return toResponse(leaveRepository.findById(id).orElseThrow(() -> new LeaveNotFoundException(id)));
    }

    @Override public LeaveResponse createLeave(CreateLeaveRequest request) {
        validateDateRange(request.getStartDate(), request.getEndDate());
        LeaveRequest leave = LeaveRequest.builder()
                .employeeId(request.getEmployeeId())
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status("PENDING")
                .build();
        return toResponse(leaveRepository.save(leave));
    }

    @Override public LeaveResponse updateLeave(Long id, UpdateLeaveRequest request) {
        validateDateRange(request.getStartDate(), request.getEndDate());
        LeaveRequest leave = leaveRepository.findById(id).orElseThrow(() -> new LeaveNotFoundException(id));
        leave.setEmployeeId(request.getEmployeeId());
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());
        leave.setStatus(request.getStatus());
        return toResponse(leaveRepository.save(leave));
    }

    @Override public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) throw new LeaveNotFoundException(id);
        leaveRepository.deleteById(id);
    }

    private void validateDateRange(java.time.LocalDate start, java.time.LocalDate end) {
        if (end.isBefore(start)) throw new IllegalArgumentException("End date must be on or after start date");
    }

    private LeaveResponse toResponse(LeaveRequest leave) {
        return LeaveResponse.builder().id(leave.getId()).employeeId(leave.getEmployeeId())
                .leaveType(leave.getLeaveType()).startDate(leave.getStartDate())
                .endDate(leave.getEndDate()).reason(leave.getReason()).status(leave.getStatus()).build();
    }
}
