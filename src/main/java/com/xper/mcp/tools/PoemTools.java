package com.xper.mcp.tools;

import java.util.List;
import java.util.Map;

public class PoemTools {

    public static final List<Map<String, Object>> TOOLS = List.of(

            Map.of(
                    "name", "get_all_poems",
                    "description", "Fetch all poems",
                    "inputSchema", Map.of(
                            "type", "object",
                            "properties", Map.of(),
                            "required", List.of()
                    )
            ),

            Map.of(
                    "name", "get_poem_by_title",
                    "description", "Fetch a poem by title",
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
            )
    );
}
