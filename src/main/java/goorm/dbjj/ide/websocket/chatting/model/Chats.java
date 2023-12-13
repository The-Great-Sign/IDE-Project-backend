package goorm.dbjj.ide.websocket.chatting.model;

import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Chats {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectUserId;
    @Column(length = 255, nullable = false)
    private String content;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Chats(ChattingContentRequestDto chatsDto) {
        this.content = chatsDto.getContent();
    }
}
