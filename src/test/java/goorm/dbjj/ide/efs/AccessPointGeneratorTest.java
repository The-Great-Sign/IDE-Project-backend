package goorm.dbjj.ide.efs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class AccessPointGeneratorTest {

    @Autowired
    private EfsAccessPointUtilImpl accessPointGenerator;

    @Test
    void generateAccessPoint() {
        String projectId = "testProjectId";
        String accessPointId = accessPointGenerator.generateAccessPoint(projectId);
        System.out.println(accessPointId);

        accessPointGenerator.deleteAccessPoint(accessPointId);
    }
}