package goorm.dbjj.ide.lambdahandler.containerstatus;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerStatus;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerStatusChangeRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class LoadContainerControllerTest {

    @Autowired
    ContainerStore containerStore;

    @Autowired
    ContainerStatusController loadContainerController;

    @Value("${aws.lambda.secretKey}")
    String secretKey;

    @AfterEach
    void tearDown() {
        containerStore = new MemoryContainerStore();
    }


    @Test
    void statusChangeRequest() {

        containerStore.save("projectId", "containerId");

        ContainerStatusChangeRequestDto containerStatusChangeRequestDto = new ContainerStatusChangeRequestDto();
        containerStatusChangeRequestDto.setTaskArn("containerId");
        containerStatusChangeRequestDto.setTaskDefinitionArn("containerImageId");
        containerStatusChangeRequestDto.setContainerStatus("RUNNING");


        assertThat(containerStore.find("projectId").getStatus()).isEqualTo(ContainerStatus.PENDING);

        loadContainerController.getContainerStatus(containerStatusChangeRequestDto,secretKey);

        assertThat(containerStore.find("projectId").getStatus()).isEqualTo(ContainerStatus.RUNNING);
    }

    @Test
    void statusChangeRequestFail() throws IOException {
        ContainerStatusChangeRequestDto containerStatusChangeRequestDto = new ContainerStatusChangeRequestDto();
        containerStatusChangeRequestDto.setTaskArn("containerId");
        containerStatusChangeRequestDto.setTaskDefinitionArn("containerImageId");
        containerStatusChangeRequestDto.setContainerStatus("RUNNING");

        assertThatThrownBy(
                () -> loadContainerController.getContainerStatus(containerStatusChangeRequestDto,secretKey)
        ).isInstanceOf(BaseException.class).hasMessage("실행중이지 않은 프로젝트로부터 변경사항이 전달되었습니다. 빠른 확인 부탁드립니다.");
    }
}