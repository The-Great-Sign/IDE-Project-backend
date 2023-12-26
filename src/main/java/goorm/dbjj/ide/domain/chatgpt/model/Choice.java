package goorm.dbjj.ide.domain.chatgpt.model;

import lombok.Data;

@Data
public class Choice {
    private int index;
    private Message message;
    private Object logprobs; // 'null' 값을 처리하기 위해 Object 타입 사용
    private String finish_reason;

    // 기본 생성자, getter, setter 생략
}