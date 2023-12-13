package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.SubscribeSessionMapper;
import goorm.dbjj.ide.websocket.SubscribeType;
import goorm.dbjj.ide.websocket.WebSocketUserSession;
import goorm.dbjj.ide.websocket.chatting.dto.ChatType;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import goorm.dbjj.ide.websocket.chatting.model.Chats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ChatsService {
    private final AtomicLong currentUsers; // 현재 유저가 몇명인지 나타내는 변수
    private final SubscribeSessionMapper subscribeSessionMapper;
    private final ChattingRepository chattingRepository;

    @Autowired
    public ChatsService(SubscribeSessionMapper subscribeSessionMapper, ChattingRepository chattingRepository) {
        this.currentUsers = new AtomicLong(0);
        this.subscribeSessionMapper = subscribeSessionMapper;
        this.chattingRepository = chattingRepository;
    }

    public void EnterCurrentUsers() {
        this.currentUsers.incrementAndGet();
    }

    public void ExitCurrentUsers(){
        long currentValue = this.currentUsers.decrementAndGet();

        // 음수 값 보정
        if (currentValue < 0) {
            this.currentUsers.set(0);
        }
    }

    /**
     * 클라이언트가 채팅방 입장 알림 및 세션 등록하기
     * */
    public ChattingResponseDto enter(SimpMessageHeaderAccessor headerAccessor, Long projectId, Long userId) {
        log.trace("ChatsService.enter execute");
        EnterCurrentUsers();

                /*      db 조회 하기로 userId 반환 없다면
        projectUsersRepository.find(userId,ProjectId).orElseThrow(() -> new BaseException("없는 프로젝트 입니다.")
        있을 경우 아래로 계속 진행.*/
        subscribeSessionMapper.addSession(headerAccessor, projectId, userId, SubscribeType.CHATTING);

        String content = userId + "유저님이 참여하였습니다.";
        return ChattingResponseDto.builder()
                .currentUsers(this.currentUsers.get())
                .messageType(ChatType.ENTER)
                .projectUserId(userId)
                .content(content)
                .build();
    }

    /**
     * 클라이언트가 채팅시 모든 메세지 전송, 데이터베이스에 저장하기
     * */
    @Transactional
    public ChattingResponseDto talk(ChattingContentRequestDto chatsDto, Long userId) {
        log.trace("ChatsService.talk execute");

        // db에 채팅 기록 저장하기
        chattingRepository.save(new Chats(chatsDto));

        return ChattingResponseDto.builder()
                .currentUsers(this.currentUsers.get())
                .messageType(ChatType.TALK)
                .projectUserId(userId)
                .content(chatsDto.getContent())
                .build();
    }

    /**
     * 클라이언트가 채팅종료 시 퇴장 알림
     * */
    public Optional<ChattingResponseDto> exit(WebSocketUserSession webSocketUserSession) {
        log.trace("ChatsService.talk execute");
        ExitCurrentUsers();

        // 채팅에 참여한 현원이 0명일 경우
        if(this.currentUsers.get() == 0L){
            return Optional.empty();
        }

        Long userId = webSocketUserSession.getUserId();

        String content = userId + "유저님이 퇴장하였습니다.";
        return Optional.ofNullable(ChattingResponseDto.builder()
                .currentUsers(this.currentUsers.get())
                .messageType(ChatType.EXIT)
                .projectUserId(userId)
                .content(content)
                .build());
    }

}
