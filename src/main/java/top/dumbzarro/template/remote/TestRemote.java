package top.dumbzarro.template.remote;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TestRemote {
    private final WebClient webClient = WebClient.create("https://localhost:8080");

    public Flux<String> chat() {
        return webClient.get()
                .uri("/api/chat")
                .retrieve()
                .bodyToFlux(String.class);
    }

    // 调用 Flux 接口（返回多个用户）
    public Flux<String> fetchAllUsers() {
        return webClient.get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(String.class);
    }

    // 调用 Mono 接口（返回单个用户）
    public String> fetchUserById(Long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
    }


}
