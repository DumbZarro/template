package top.dumbzarro.template.controller.test;

import top.dumbzarro.template.config.AppConfig;
import top.dumbzarro.template.controller.biz.request.ChatRequest;
import top.dumbzarro.template.repository.entity.UserBasicInfoEntity;
import top.dumbzarro.template.repository.postgre.UserBasicInfoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "测试", description = "测试控制器")
@Slf4j
@RestController()
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MyTestController {

    private final ChatClient chatClient;
    private final AppConfig appConfig;

    private final UserBasicInfoRepository userBasicInfoRepository;


    @PreAuthorize("hasRole('admin')")
    @Operation(description = "chat")
    @GetMapping(value = "/chat/{prompt}")
    public Flux<String> chat(@PathVariable String prompt) {
        return chatClient.prompt(prompt).stream().content();
    }

    @Operation(description = "postchat")
    @PostMapping(value = "/post/chat")
    public Flux<String> chat(@RequestBody ChatRequest request) {
        return chatClient.prompt(request.getPrompt())
                .messages(request.getMessages()).stream().content();
    }

    @Operation(description = "test")
    @PostMapping(value = "/test")
    public String> test(@RequestBody ChatRequest request) {
        UserBasicInfoEntity> userBasicInfoEntitiesByEmail = userBasicInfoRepository.findByEmail("emial");
        userBasicInfoEntitiesByEmail.subscribe(System.out::println, System.err::println, () -> {
            System.out.println("done");
        });
        return Mono.empty();
    }


}
