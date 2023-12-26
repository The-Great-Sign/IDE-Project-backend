package goorm.dbjj.ide.domain.chatgpt.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatGptAskRequest {
    private String model = "gpt-3.5-turbo";
    private List<Message> messages;

    public static ChatGptAskRequest create(Message ... messages) {
        ChatGptAskRequest request = new ChatGptAskRequest();
        request.setMessages(List.of(messages));
        return request;
    }
}
