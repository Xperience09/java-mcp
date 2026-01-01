package com.xper.mcp.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;

@RestController
public class RootMcpSseController {

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping(
            value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter rootSse(@RequestBody String body) {
        SseEmitter emitter = new SseEmitter(5_000L);

        try {
            JsonNode req = mapper.readTree(body);
            String method = req.get("method").asText();
            JsonNode idNode = req.get("id");

            String response;

            switch (method) {
                case "initialize" -> response = """
                {
                  "jsonrpc": "2.0",
                  "id": %s,
                  "result": {
                    "protocolVersion": "2024-11-05",
                    "capabilities": {
                      "tools": {}
                    },
                    "serverInfo": {
                      "name": "java-mcp",
                      "version": "0.1.0"
                    }
                  }
                }
                """.formatted(idNode);

                case "tools/list" -> response = """
                {
                  "jsonrpc": "2.0",
                  "id": %s,
                  "result": {
                    "tools": []
                  }
                }
                """.formatted(idNode);

                default -> response = """
                {
                  "jsonrpc": "2.0",
                  "id": %s,
                  "error": {
                    "code": -32601,
                    "message": "Method not found"
                  }
                }
                """.formatted(idNode);
            }

            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(response));

            emitter.complete();
            return emitter;

        } catch (Exception e) {
            emitter.completeWithError(e);
            return emitter;
        }
    }
}
