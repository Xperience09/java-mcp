package com.xper.mcp.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@RestController
public class McpController {

    @GetMapping({"/handshake", "/.well-known/mcp/handshake"})
    public Map<String, Object> handshake() {
        return Map.of("name", "local-mcp-server", "version", "0.1.0", "capabilities", List.of("tools"));
    }

    @GetMapping(value = {"/events", "/.well-known/mcp/events"}, produces = "text/event-stream")
    public SseEmitter events() {
        SseEmitter emitter = new SseEmitter(0L);

        try {
            emitter.send(SseEmitter.event().name("ready").data("connected"));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @GetMapping({"/tools", "/.well-known/mcp/tools"})
    public Map<String, Object> tools() {
        return Map.of("tools", List.of(Map.of("name", "ping", "description", "Returns a sample string", "inputSchema", Map.of("type", "object", "properties", Map.of()))));
    }
}
