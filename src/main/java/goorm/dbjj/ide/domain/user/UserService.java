package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(email+"의 이메일을 가진 사용자가 없습니다."));
    }
}
