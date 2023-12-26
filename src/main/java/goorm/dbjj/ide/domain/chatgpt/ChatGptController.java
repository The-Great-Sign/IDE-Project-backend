package goorm.dbjj.ide.domain.chatgpt;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.chatgpt.model.ChatGptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatgpt")
public class ChatGptController {

    private final ChatGptService chatGptService;

    @PostMapping("/ask")
    public ApiResponse<String> ask(String question) {
        ChatGptResponse resp = chatGptService.ask(question);
        return ApiResponse.ok(resp.getChoices()[0].getMessage().getContent());
    }

    @PostMapping("/review-file/{fileId}")
    public ApiResponse<String> review(
            @PathVariable Long fileId
    ) {
        ChatGptResponse chatGptResponse = chatGptService.codeReview(fileId);
        return ApiResponse.ok(chatGptResponse.getChoices()[0].getMessage().getContent());
    }
}
