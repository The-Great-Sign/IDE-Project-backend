package goorm.dbjj.ide.container;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.List;
import java.util.Map;


/**
 * Note: 결국 프로젝트를 생성하기 위해 해야하는 일
 * RDS에 프로젝트 메타데이터 설정
 * EFS에 baseDirectory 생성한 뒤, access point 생성 후 저장 - arn 저장
 * ECS에 TaskDefinition 설정 - taskArn 저장
 *
 */
@SpringBootTest
class ContainerUtilStudyTest {

    @Autowired
    private EcsClient ecsClient;

    @Test
    void registerTaskDefinition() {

        LogConfiguration logConfiguration = LogConfiguration.builder()
                .logDriver(LogDriver.AWSLOGS)
                .options(Map.of(
                        "awslogs-create-group", "true",
                        "awslogs-group", "/ecs/ide",   // CloudWatch Logs 로그 그룹 이름
                        "awslogs-region", "ap-northeast-2",    // 로그 그룹이 위치한 리전
                        "awslogs-stream-prefix", "ecs"    // 로그 스트림의 접두사
                ))
                .build();

        List<MountPoint> mountPoints = List.of(MountPoint.builder()
                .containerPath("/app")
                .sourceVolume("efs")
                .readOnly(false)
                .build());

        ContainerDefinition containerDefinition = ContainerDefinition.builder()
                .logConfiguration(logConfiguration)
                .name("test")
                .image("python:3")
                .cpu(512)
                .memory(1024)
                .memoryReservation(1024)
                .essential(true)
                .command("python", "-m", "http.server")
                .linuxParameters(linux -> linux.initProcessEnabled(true))
                .mountPoints(mountPoints)
                .build();

        EFSAuthorizationConfig authorizationConfig = EFSAuthorizationConfig.builder()
                .accessPointId("fsap-04fbb958d168856f3") //5
                .iam("DISABLED")
                .build();

        EFSVolumeConfiguration efsVolumeConfiguration = EFSVolumeConfiguration.builder()
                .authorizationConfig(authorizationConfig)
                .fileSystemId("fs-0ea29637c487e27cd")
                .transitEncryption("ENABLED")
                .rootDirectory("/")
                .build();

        Volume volume = Volume.builder()
                .efsVolumeConfiguration(efsVolumeConfiguration)
                .name("efs")
                .build();


        RegisterTaskDefinitionRequest taskDefRequest = RegisterTaskDefinitionRequest.builder()
                .containerDefinitions(containerDefinition)
                .volumes(volume)
                .taskRoleArn("arn:aws:iam::092624380570:role/taskRole")
                .executionRoleArn("arn:aws:iam::092624380570:role/taskExecutionRole")
                .requiresCompatibilities(Compatibility.FARGATE)
                .networkMode(NetworkMode.AWSVPC)
                .cpu("512")
                .memory("1024")
                .family("python")
                .build();


//        ListTaskDefinitionsResponse listTaskDefinitionsResponse = ecsClient.listTaskDefinitions();
//        List<String> taskDefinitionArns = listTaskDefinitionsResponse.taskDefinitionArns();
        RegisterTaskDefinitionResponse taskDefResponse = ecsClient.registerTaskDefinition(taskDefRequest);

        TaskDefinition taskDefinition = taskDefResponse.taskDefinition();

        System.out.println(taskDefinition.toString());
        System.out.println("taskDefinition.taskDefinitionArn() = " + taskDefinition.taskDefinitionArn());

//        System.out.println(taskDefinition.taskDefinitionArn());
//
//        CreateServiceRequest req = CreateServiceRequest.builder()
//                .cluster("test")
//                .desiredCount(1)
//                .launchType("FARGATE")
//                .serviceName("test")
//                .taskDefinition(taskDefinition.taskDefinitionArn())
//                .build();
    }

    @Test
    void runTask() {

        NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
                .awsvpcConfiguration(AwsVpcConfiguration.builder()
                        .subnets("subnet-0354880eae3c97d24")
                        .securityGroups("sg-02c77f45ba36d8155")
                        .assignPublicIp(AssignPublicIp.ENABLED) // 공용 IP 할당 (ENABLED 또는 DISABLED)
                        .build())
                .build();


        RunTaskRequest runTaskRequest = RunTaskRequest.builder()
                .cluster("IDE_CONTAINER")
                .taskDefinition("inaws")
                .launchType(LaunchType.FARGATE)
                .networkConfiguration(networkConfiguration)
                .enableExecuteCommand(true)
                .build();

        RunTaskResponse runTaskResponse = ecsClient.runTask(runTaskRequest);

        List<Task> tasks = runTaskResponse.tasks();
        for (Task task : tasks) {
            String s = task.taskDefinitionArn();
            System.out.println("arn = " + s);
        }

    }

    @Test
    void showTasks() {

        ListTasksRequest listTasksRequest = ListTasksRequest.builder()
                .cluster("IDE_CONTAINER")
                .build();

        ListTasksResponse listTasksResponse = ecsClient.listTasks(listTasksRequest);

        List<String> taskArns = listTasksResponse.taskArns();
        for (String taskArn : taskArns) {
            System.out.println("taskArn = " + taskArn);
        }
    }

    @Test
    void execute() {

        ExecuteCommandRequest executeCommandRequest = ExecuteCommandRequest.builder()
                .cluster("IDE_CONTAINER")
                .task("arn:aws:ecs:ap-northeast-2:092624380570:task/IDE_CONTAINER/8b6a9380942c4fb8b5fcba2c6b14a450")
//                .container("test")
                .interactive(true)
                .command("python ./app/hello.py")
                .build();

        ExecuteCommandResponse response = ecsClient.executeCommand(executeCommandRequest);

        System.out.println(response.toString());

    }
}