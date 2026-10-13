package com.example.leavemanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createLeaveRequest(String employeeId) throws Exception {
        return """
                {
                    "employeeId": "%s",
                    "leaveType": "ANNUAL",
                    "startDate": "2026-11-02",
                    "endDate": "2026-11-04",
                    "reason": "Family vacation"
                }
                """.formatted(employeeId);
    }

    private long createLeave(String employeeId) throws Exception {
        MvcResult result = mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLeaveRequest(employeeId)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString());

        return response.get("id").asLong();
    }

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts.
    }

    // CREATE: POST /leaves
    @Test
    void shouldCreateLeave() throws Exception {
        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLeaveRequest("EMP-CREATE")))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.employeeId").value("EMP-CREATE"))
                .andExpect(jsonPath("$.leaveType").value("ANNUAL"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // READ ALL: GET /leaves
    @Test
    void shouldGetAllLeaves() throws Exception {
        createLeave("EMP-READ-ALL");

        mockMvc.perform(get("/leaves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.employeeId == 'EMP-READ-ALL')]")
                        .isNotEmpty());
    }

    // READ BY ID: GET /leaves/{id}
    @Test
    void shouldGetLeaveById() throws Exception {
        long id = createLeave("EMP-READ-ID");

        mockMvc.perform(get("/leaves/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.employeeId").value("EMP-READ-ID"));
    }

// UPDATE: PUT /leaves/{id}
    @Test
    void shouldUpdateLeave() throws Exception {
        long id = createLeave("EMP-UPDATE");

        String updateRequest = """
            {
                "employeeId": "EMP-UPDATE",
                "leaveType": "SICK",
                "startDate": "2026-11-05",
                "endDate": "2026-11-06",
                "reason": "Medical appointment",
                "status": "PENDING"
            }
            """;

        mockMvc.perform(put("/leaves/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.leaveType").value("SICK"))
                .andExpect(jsonPath("$.reason")
                        .value("Medical appointment"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // DELETE: DELETE /leaves/{id}
    @Test
    void shouldDeleteLeave() throws Exception {
        long id = createLeave("EMP-DELETE");

        mockMvc.perform(delete("/leaves/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/leaves/{id}", id))
                .andExpect(status().isNotFound());
    }
}