package com.xper.mcp.client;

import com.xper.mcp.model.Poem;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class PoemBackendClient {

    private final WebClient webClient;

    public PoemBackendClient(WebClient poemBackendWebClient) {
        this.webClient = poemBackendWebClient;
    }

    public List<Poem> getAllPoems() {
        return webClient
                .get()
                .uri("/Poem/getAll")
                .retrieve()
                .bodyToFlux(Poem.class)
                .collectList()
                .block();
    }

    public Poem getPoemByTitle(String title) {
        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/Poem/get")
                                .queryParam("title", title)
                                .build()
                )
                .retrieve()
                .bodyToMono(Poem.class)
                .block();
    }
}
