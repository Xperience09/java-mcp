package com.xper.mcp.tools;

import com.xper.mcp.client.PoemBackendClient;
import com.xper.mcp.model.Poem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PoemToolHandler {

    private final PoemBackendClient client;

    public PoemToolHandler(PoemBackendClient client) {
        this.client = client;
    }

    /**
     * IMPORTANT:
     * This method returns ONLY content blocks.
     * The MCP envelope is added by PoemToolExecutor.
     */
    public List<Map<String, Object>> handle(String toolName, Map<String, Object> arguments) {

        return switch (toolName) {
            case "get_all_poems" -> getAllPoems();
            case "get_poem_by_title" -> getPoemByTitle(arguments);
            case "add_poem" -> addPoem(arguments);
            default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
        };
    }

    private List<Map<String, Object>> getAllPoems() {

        List<Poem> poems = client.getAllPoems();

        String text = poems.stream()
                .map(p -> "Title: " + p.getTitle() + "\n" + p.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        return List.of(
                Map.of(
                        "type", "text",
                        "text", text
                )
        );
    }

    private List<Map<String, Object>> getPoemByTitle(Map<String, Object> arguments) {

        String title = (String) arguments.get("title");

        Poem poem = client.getPoemByTitle(title);

        return List.of(
                Map.of(
                        "type", "text",
                        "text", "Title: " + poem.getTitle() + "\n\n" + poem.getContent()
                )
        );
    }

    private List<Map<String, Object>> addPoem(Map<String, Object> arguments) {

        String title = (String) arguments.get("title");
        String content = (String) arguments.get("content");

        Poem poem = new Poem();
        poem.setTitle(title);
        poem.setContent(content);

        Poem addedPoem = client.addPoem(poem);

        return List.of(
                Map.of(
                        "type", "text",
                        "text", "Added Poem:\nTitle: " + addedPoem.getTitle() + "\n\n" + addedPoem.getContent()
                )
        );
    }
}
