package goorm.dbjj.ide.domain.chatgpt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String role;
    private String content;

    // 기본 생성자, getter, setter 생략
}