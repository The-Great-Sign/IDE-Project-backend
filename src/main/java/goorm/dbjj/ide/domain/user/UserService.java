package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String email){
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }
}
