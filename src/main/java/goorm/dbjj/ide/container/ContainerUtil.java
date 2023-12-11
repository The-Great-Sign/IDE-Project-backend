package goorm.dbjj.ide.container;

public interface ContainerUtil {
    void executeCommand(String containerId, String command);

    String createContainerImage(ProgrammingLanguage programmingLanguage, String accessPointId);

    String runContainer(String containerImageId);

    void stopContainer(String containerId);
}
