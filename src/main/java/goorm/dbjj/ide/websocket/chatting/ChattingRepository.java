package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.model.Chats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRepository extends JpaRepository<Chats,Long> {
}
