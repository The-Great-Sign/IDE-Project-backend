package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.websocket.WebSocketProjectUserCountMapper;
import goorm.dbjj.ide.websocket.chatting.dto.ChatType;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import goorm.dbjj.ide.websocket.chatting.model.Chat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRepository chattingRepository;
    private final WebSocketProjectUserCountMapper webSocketProjectUserCountMapper;

    /**
     * 클라이언트가 채팅방 입장 알림 및 세션 등록하기
     * */
    public ChattingResponseDto enter(
            String projectId,
            String userNickname
    ) {
        String content = userNickname + "님이 참여하였습니다.";

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new BaseException("채팅 참여 메세지 Thread.sleep 에러");
        }

        return ChattingResponseDto.builder()
                .messageType(ChatType.ENTER)
                .userNickname(userNickname)
                .content(content)
                .currentUsers(this.webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId))
                .build();
    }

    /**
     * 클라이언트가 채팅시 모든 메세지 전송, 데이터베이스에 저장하기
     * */
    @Transactional
    public ChattingResponseDto talk(
            ChattingContentRequestDto chattingContentRequestDto,
            String userNickname,
            String projectId
    ) {
        // db에 채팅 기록 저장하기
        chattingRepository.save(new Chat(chattingContentRequestDto));

        return ChattingResponseDto.builder()
                .messageType(ChatType.TALK)
                .userNickname(userNickname)
                .content(chattingContentRequestDto.getContent())
                .currentUsers(this.webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId))
                .build();
    }

    /**
     * 클라이언트가 채팅종료 시 퇴장 알림
     * */
    public ChattingResponseDto exit(
            String userNickname,
            String projectId
    ){
        String content = userNickname + "님이 퇴장하였습니다.";

        return ChattingResponseDto.builder()
                .messageType(ChatType.EXIT)
                .userNickname(userNickname)
                .content(content)
                .currentUsers(this.webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId))
                .build();
    }
}
