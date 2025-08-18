package top.dumbzarro.template.controller.biz;


import top.dumbzarro.template.controller.biz.request.ChatRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.service.RSocketExchange;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RSocketExchange
@AllArgsConstructor
public class RsocketController {

    private final WebClient webClient;

    @Operation(description = "test")
    @MessageMapping(value = "/test")
    public String> test2(@RequestBody ChatRequest request) {

        URI url = URI.create("https://example.org:8080/rsocket");
        RSocketStrategies strategies = RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .build();
        RSocketRequester requester = RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .websocket(url);
        return Mono.empty();
    }
}
