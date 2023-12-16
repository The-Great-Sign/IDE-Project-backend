package goorm.dbjj.ide.websocket.chatting.model;

import goorm.dbjj.ide.domain.project.model.ProjectUser;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "Chats")
public class Chat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_user_id", insertable = false, updatable = false)
    private ProjectUser projectUserId;
    @Column(length = 255, nullable = false)
    private String content;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Chat(ChattingContentRequestDto chatsDto) {
        this.content = chatsDto.getContent();
    }
}
