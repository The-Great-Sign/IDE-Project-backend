package goorm.dbjj.ide.websocket.chatting.model;

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
}
