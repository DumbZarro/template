package top.dumbzarro.template.controller.biz.request;

import lombok.Data;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@Data
public class ChatRequest {
    String prompt;

    List<Message> messages;
}
