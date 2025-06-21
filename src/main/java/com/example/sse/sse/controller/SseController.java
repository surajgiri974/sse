package com.example.sse.sse.controller;

import com.example.sse.sse.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
public class SseController {

    private final SseService sseService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return sseService.addClient();
    }

    @PostMapping(value = "/sse/report")
    public ResponseEntity<String> create(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        jdbcTemplate.update("INSERT INTO report (content) VALUES (?)", content);
        return ResponseEntity.ok("Inserted");
    }


}