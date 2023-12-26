package goorm.dbjj.ide.domain.chatgpt;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.chatgpt.model.ChatGptAskRequest;
import goorm.dbjj.ide.domain.chatgpt.model.ChatGptResponse;
import goorm.dbjj.ide.domain.chatgpt.model.Message;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadata;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadataRepository;
import goorm.dbjj.ide.domain.fileDirectory.id.model.PathGenerator;
import goorm.dbjj.ide.domain.project.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGptService {

    @Value("${app.openai-api-key}")
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate;
    private final FileMetadataRepository fileMetadataRepository;
    private final PathGenerator pathGenerator;


    public ChatGptResponse ask(String question) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + OPENAI_API_KEY);

        ChatGptAskRequest request = ChatGptAskRequest.create(
                new Message("system", "You are a developer. Please answer in Korean."),
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

    public ChatGptResponse codeReview(Long fileId) {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new BaseException("파일이 존재하지 않습니다."));

        Project project = fileMetadata.getProject();
        String path = fileMetadata.getPath();

        File file = new File(pathGenerator.getPath(project.getId(), path));

        if(!file.exists()) {
            throw new BaseException("파일이 존재하지 않습니다.");
        }

        //get file content and ask
        try {

            String content = FileUtils.readFileToString(file, "UTF-8");
            return ask(content + "\n 이 코드에 대한 코드리뷰를 수행해줘.");
        } catch (IOException e) {
            throw new BaseException("파일을 읽는데 실패했습니다.");
        }
    }
}
