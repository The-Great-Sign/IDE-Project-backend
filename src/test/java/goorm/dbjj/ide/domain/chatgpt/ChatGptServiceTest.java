package goorm.dbjj.ide.domain.chatgpt;

import goorm.dbjj.ide.domain.chatgpt.model.ChatGptResponse;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadata;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatGptServiceTest {

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Test
    void review() {
        ChatGptResponse chatGptResponse = chatGptService.codeReview(1L);
        System.out.println(chatGptResponse);
    }

    @Test
    void ask() {
        ChatGptResponse chatGptResponse = chatGptService.ask("What is the best programming language?");
        System.out.println(chatGptResponse);
    }






}