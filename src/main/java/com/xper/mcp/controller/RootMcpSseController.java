package com.xper.mcp.controller;

import com.xper.mcp.tools.PoemToolExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RestController
public class RootMcpSseController {

    private final PoemToolExecutor executor;

    public RootMcpSseController(PoemToolExecutor executor) {
        this.executor = executor;
    }

    @PostMapping(path = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter root(@RequestBody Map<String, Object> payload) throws IOException {

        String method = (String) payload.get("method");

        // 🔴 IMPORTANT: Ignore notifications completely
        if ("notifications/initialized".equals(method)) {
            return null; // no SSE, no response
        }

        SseEmitter emitter = new SseEmitter(0L);

        if ("initialize".equals(method)) {
            sendInitialize(payload, emitter);
            return emitter;
        }
        if ("tools/list".equals(method)) {
            emitter.send(
                    SseEmitter.event()
                            .name("message")
                            .data(Map.of(
                                    "jsonrpc", "2.0",
                                    "id", payload.get("id"),
                                    "result", Map.of(
                                            "tools", toolsDefinition()
                                    )
                            ))
            );
            return emitter;
        }


        CompletableFuture.runAsync(() -> handleAsync(payload, emitter));
        return emitter;
    }

    private void sendInitialize(Map<String, Object> payload, SseEmitter emitter) {
        try {
            emitter.send(
                    SseEmitter.event()
                            .name("message")
                            .data(Map.of(
                                    "jsonrpc", "2.0",
                                    "id", payload.get("id"),
                                    "result", Map.of(
                                            "protocolVersion", "2025-06-18",
                                            "capabilities", Map.of(
                                                    "tools", Map.of()
                                            ),
                                            "serverInfo", Map.of(
                                                    "name", "java-mcp",
                                                    "version", "1.0.0"
                                            ),
                                            "tools", toolsDefinition()
                                    )
                            ))
            );
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }


    private void handleAsync(Map<String, Object> payload, SseEmitter emitter) {
        try {
            Map<String, Object> response = executor.execute(payload);

            emitter.send(
                    SseEmitter.event()
                            .name("message")
                            .data(response)
            );
        } catch (Exception e) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("message")
                                .data(Map.of(
                                        "jsonrpc", "2.0",
                                        "id", payload.get("id"),
                                        "error", Map.of(
                                                "code", -32603,
                                                "message", e.getMessage()
                                        )
                                ))
                );
            } catch (Exception ignored) {
            }
        }
    }
    private List<Map<String, Object>> toolsDefinition() {
        return List.of(
                Map.of(
                        "name", "get_all_poems",
                        "description", "Get all poems",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(),
                                "required", List.of()
                        )
                ),
                Map.of(
                        "name", "get_poem_by_title",
                        "description", "Get a poem by its title",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "title", Map.of(
                                                "type", "string",
                                                "description", "Title of the poem"
                                        )
                                ),
                                "required", List.of("title")
                        )
                ),
                Map.of(
                        "name", "add_poem",
                        "description", "Add a new poem",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "title", Map.of(
                                                "type", "string",
                                                "description", "Title of the poem"
                                        ),
                                        "content", Map.of(
                                                "type", "string",
                                                "description", "Content of the poem"
                                        )
                                ),
                                "required", List.of("title", "content")
                        )
                )
        );
    }

}
