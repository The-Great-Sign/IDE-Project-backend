package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.dto.ChatType;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import goorm.dbjj.ide.websocket.chatting.model.Chats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatsService {
    private final ChattingRepository chattingRepository;
    private final ChattingCurrentUserMapper chattingCurrentUserMapper;

    /**
     * 클라이언트가 채팅방 입장 알림 및 세션 등록하기
     * */
    public ChattingResponseDto enter(Long projectId, Long userId) {
        log.trace("ChatsService.enter execute");
        // 프로젝트 인원 수 증가
        chattingCurrentUserMapper.enterProjectCurrentUserMapper(projectId);
        String content = userId + "유저님이 참여하였습니다.";

        return ChattingResponseDto.builder()
                .messageType(ChatType.ENTER)
                .content(content)
                .currentUsers(this.chattingCurrentUserMapper.getProjectCurrentUserMapper(projectId))
                .build();
    }

    /**
     * 클라이언트가 채팅시 모든 메세지 전송, 데이터베이스에 저장하기
     * */
    @Transactional
    public ChattingResponseDto talk(ChattingContentRequestDto chattingContentRequestDto, Long userId) {
        log.trace("ChatsService.talk execute");

        // db에 채팅 기록 저장하기
        chattingRepository.save(new Chats(chattingContentRequestDto));

        return ChattingResponseDto.builder()
                .messageType(ChatType.TALK)
                .UserId(userId)
                .content(chattingContentRequestDto.getContent())
                .build();
    }

    /**
     * 클라이언트가 채팅종료 시 퇴장 알림
     * */
    public Optional<ChattingResponseDto> exit(Long userId, Long projectId) {
        log.trace("ChatsService.exit execute");

        // subscribe 안 하거나(subscribe 하기전에 클라이언트가 강제종료) 인원이 0명인 경우 에러 처리
        if(chattingCurrentUserMapper.getProjectCurrentUserMapper(projectId) == null
        || chattingCurrentUserMapper.getProjectCurrentUserMapper(projectId) == 0L){
            return Optional.empty();
        }

        chattingCurrentUserMapper.exitProjectCurrentUserMapper(projectId);
        String content = userId + "유저님이 퇴장하였습니다.";

        return Optional.ofNullable(ChattingResponseDto.builder()
                .messageType(ChatType.EXIT)
                .content(content)
                .currentUsers(this.chattingCurrentUserMapper.getProjectCurrentUserMapper(projectId))
                .build());
    }

}
