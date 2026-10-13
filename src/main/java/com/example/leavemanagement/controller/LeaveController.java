package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.*;
import com.example.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(@Valid @RequestBody CreateLeaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.createLeave(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponse> updateLeave(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateLeaveRequest request) {
        return ResponseEntity.ok(leaveService.updateLeave(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }
}
