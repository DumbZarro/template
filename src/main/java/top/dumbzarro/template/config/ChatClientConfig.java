package top.dumbzarro.template.config;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ChatClientConfig {

    private final ChatClient.Builder builder;

    @Bean
    public ChatClient ChatClient() {
        return builder.build();
    }
}
