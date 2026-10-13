package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.LeaveResponse;
import com.example.leavemanagement.exception.GlobalExceptionHandler;
import com.example.leavemanagement.service.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveController.class)
@Import(GlobalExceptionHandler.class)
class LeaveControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private LeaveService leaveService;

    @Test void getAllLeavesReturnsList() throws Exception {
        when(leaveService.getAllLeaves()).thenReturn(List.of(LeaveResponse.builder().id(1L)
                .employeeId("EMP-1").leaveType("ANNUAL").startDate(LocalDate.parse("2026-11-01"))
                .endDate(LocalDate.parse("2026-11-02")).reason("Trip").status("PENDING").build()));
        mockMvc.perform(get("/leaves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test void createLeaveValidatesRequiredFields() throws Exception {
        mockMvc.perform(post("/leaves").contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeId\":\"\",\"leaveType\":\"ANNUAL\",\"startDate\":\"2026-11-01\",\"endDate\":\"2026-11-02\",\"reason\":\"Trip\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.employeeId").exists());
        verify(leaveService, never()).createLeave(any());
    }
}
