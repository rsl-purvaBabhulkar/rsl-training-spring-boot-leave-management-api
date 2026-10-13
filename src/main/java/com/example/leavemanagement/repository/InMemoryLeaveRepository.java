package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.LeaveRequest;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLeaveRepository implements LeaveRepository {
    private final Map<Long, LeaveRequest> leaves = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override public List<LeaveRequest> findAll() { return new ArrayList<>(leaves.values()); }
    @Override public Optional<LeaveRequest> findById(Long id) {
        return id == null ? Optional.empty() : Optional.ofNullable(leaves.get(id));
    }
    @Override public LeaveRequest save(LeaveRequest leave) {
        if (leave.getId() == null) leave.setId(idGenerator.getAndIncrement());
        leaves.put(leave.getId(), leave);
        return leave;
    }
    @Override public boolean existsById(Long id) { return id != null && leaves.containsKey(id); }
    @Override public void deleteById(Long id) { if (id != null) leaves.remove(id); }
}
