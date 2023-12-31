package goorm.dbjj.ide.domain.chatgpt;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.chatgpt.model.ChatGptResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatgpt")
public class ChatGptController {

    private final ChatGptService chatGptService;

    @Data
    public static class AskRequest {
        private String question;
    }

    @PostMapping("/ask")
    public ApiResponse<String> ask(
            @RequestBody AskRequest request
    ) {
        log.trace("ChatGptController.ask called : {}", request.question);
        ChatGptResponse resp = chatGptService.ask(request.question);
        return ApiResponse.ok(resp.getChoices()[0].getMessage().getContent());
    }

    @PostMapping("/review-file/{fileId}")
    public ApiResponse<String> review(
            @PathVariable Long fileId
    ) {
        log.trace("ChatGptController.review called : {}", fileId);
        ChatGptResponse chatGptResponse = chatGptService.codeReview(fileId);
        return ApiResponse.ok(chatGptResponse.getChoices()[0].getMessage().getContent());
    }
}
