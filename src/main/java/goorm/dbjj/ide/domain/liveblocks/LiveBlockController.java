package goorm.dbjj.ide.domain.liveblocks;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.api.exception.BaseException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/live-blocks")
@RequiredArgsConstructor
public class LiveBlockController {

    private final RestTemplate restTemplate;

    @Value("${liveblocks.api.secret-key}")
    private String API_SECRET_KEY;
    private final String DELETE_ROOM_URL_TEMPLATE = "https://api.liveblocks.io/v2/rooms/%s";

    private final HttpHeaders headers = new HttpHeaders();

    @PostConstruct
    public void init() {
        headers.set("Authorization", API_SECRET_KEY);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ApiResponse<Void> deleteRoom(@PathVariable String roomId) {

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    String.format(DELETE_ROOM_URL_TEMPLATE, roomId),
                    HttpMethod.DELETE,
                    entity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ApiResponse.ok();
            } else {
                throw new BaseException("LiveBlocks API 호출에 실패했습니다. : "+response.getBody());
            }

        } catch (Exception e) {
            throw new BaseException("LiveBlocks API 호출에 실패했습니다. : "+e.getMessage());
        }
    }
}
