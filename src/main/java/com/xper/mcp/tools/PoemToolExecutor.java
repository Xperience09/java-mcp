package com.xper.mcp.tools;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PoemToolExecutor {

    private final PoemToolHandler handler;

    public PoemToolExecutor(PoemToolHandler handler) {
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(Map<String, Object> payload) {

        Object id = payload.get("id");

        Map<String, Object> params = (Map<String, Object>) payload.get("params");
        String name = (String) params.get("name");
        Map<String, Object> arguments =
                (Map<String, Object>) params.getOrDefault("arguments", Map.of());

        List<Map<String, Object>> content =
                handler.handle(name, arguments);

        return Map.of(
                "jsonrpc", "2.0",
                "id", id,
                "result", Map.of(
                        "content", content
                )
        );
    }

}
