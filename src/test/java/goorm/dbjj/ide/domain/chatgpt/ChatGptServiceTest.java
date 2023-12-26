package goorm.dbjj.ide.domain.chatgpt;

import goorm.dbjj.ide.domain.chatgpt.model.ChatGptResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatGptServiceTest {

    @Autowired
    private ChatGptService chatGptService;

    @Test
    void ask() {
        ChatGptResponse chatGptResponse = chatGptService.codeReview("코드리뷰를 해야하는 이유는 무엇인가요?");
        System.out.println(chatGptResponse);
    }


}