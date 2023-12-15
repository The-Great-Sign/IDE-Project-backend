package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRepository extends JpaRepository<Chat,Long> {
}
