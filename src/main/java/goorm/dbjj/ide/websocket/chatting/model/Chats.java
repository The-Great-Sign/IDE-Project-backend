package goorm.dbjj.ide.websocket.chatting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Chats {
    @Id
    private Long id;
    private Long ProjectUserId;
    @Column(length = 255, nullable = false)
    private String content;
    @CreationTimestamp
    private LocalDateTime createAt;
}
