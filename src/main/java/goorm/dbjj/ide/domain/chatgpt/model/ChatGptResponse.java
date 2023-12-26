package goorm.dbjj.ide.domain.chatgpt.model;

import lombok.Data;
import lombok.Getter;

@Data
public class ChatGptResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private String system_fingerprint;
    private Choice[] choices;
    private Usage usage;

    // 기본 생성자, getter, setter 생략
}
