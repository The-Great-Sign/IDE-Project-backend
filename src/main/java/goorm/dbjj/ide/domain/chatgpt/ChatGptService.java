package goorm.dbjj.ide.domain.chatgpt;

import goorm.dbjj.ide.domain.chatgpt.model.ChatGptAskRequest;
import goorm.dbjj.ide.domain.chatgpt.model.ChatGptResponse;
import goorm.dbjj.ide.domain.chatgpt.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGptService {

    @Value("${app.openai-api-key}")
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate;

    public ChatGptResponse codeReview(String question) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + OPENAI_API_KEY);

        ChatGptAskRequest request = ChatGptAskRequest.create(
                new Message("system", "You are a developer. You should do code review. Please answer in Korean."),
                new Message("user", question)
        );

        HttpEntity<ChatGptAskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatGptResponse> res = restTemplate.exchange("https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                ChatGptResponse.class
        );

        ChatGptResponse chatGptResponse = res.getBody();
        log.info("chatGptResponse : {}", chatGptResponse);

        return chatGptResponse;
    }
}
