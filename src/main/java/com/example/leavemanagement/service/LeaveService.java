package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.*;
import java.util.List;

public interface LeaveService {
    List<LeaveResponse> getAllLeaves();
    LeaveResponse getLeaveById(Long id);
    LeaveResponse createLeave(CreateLeaveRequest request);
    LeaveResponse updateLeave(Long id, UpdateLeaveRequest request);
    void deleteLeave(Long id);
}
