package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 *  HandshakeInterceptor 
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandShackInterceptor implements HandshakeInterceptor {
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * http Upgrade 응답 이전에 유효한 유저인지 jwt 를 확인하고, WebSocketUserSessionMapper에 등록하는 메서드
     * */
    @Override
    @Transactional(readOnly = true)
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
         // SecuritycontextHolder을 사용해서 인증된 사용자의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자인지 확인, 아니면 exception.
        if(authentication == null && !authentication.isAuthenticated()){
            throw new BaseException("인증된 사용자가 아닙니다.");
        }

        // 인증된 사용자의 세부 정보 불러오기.
        User userDetails = (User) authentication.getPrincipal();
        UserInfoDto userInfoDto = new UserInfoDto(userDetails);
        log.trace("웹소켓 사용자 ID, 이름 가져오기 : {} {}", userInfoDto.getId(), userInfoDto.getNickname());

        // URL 마지막 부분에 존재하는 프로젝트ID 가져오기
        String[] split = request.getURI().toString().split("/");
        String projectId = split[split.length-1];
        log.trace("{}", projectId);

        Optional<Project> projectOptional = projectRepository.findById(projectId);
        // 프로젝트가 없을 경우
        if(projectOptional.isEmpty()){
            log.error("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 프로젝트에 참여한 유저가 아닐 경우
        if(!projectUserRepository.existsByProjectAndUser(projectOptional.get(), userDetails)){
            log.error("해당 프로젝트의 소유권 없는자가 접근했습니다.");
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 이미 실행중인 프로젝트인지 검증
        if(webSocketUserSessionMapper.existsByProjectAndUser(userInfoDto, projectId)){
            log.warn("이미 실행중인 프로젝트 입니다.");
            throw new BaseException("이미 실행중인 프로젝트 입니다!");
        }

        // 세션등록
        String uuid = UUID.randomUUID().toString();
        webSocketUserSessionMapper.put(uuid, new WebSocketUser(userInfoDto, projectId));

        // STOMP 메서드를 보내면 사용자 인식 가능하게 함.
        attributes.put("WebSocketUserSessionId", uuid);
        return true;
    }

    /**
     * http Upgrade 응답 이후 실행되는 부분
     * 사용 안함.
     * */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
