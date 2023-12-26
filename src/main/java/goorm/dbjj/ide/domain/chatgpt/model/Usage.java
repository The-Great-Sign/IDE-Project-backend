package goorm.dbjj.ide.domain.chatgpt.model;

import lombok.Data;

@Data
public class Usage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;

    // 기본 생성자, getter, setter 생략
}
