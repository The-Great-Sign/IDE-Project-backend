package goorm.dbjj.ide.lambdahandler.containerstatus;

import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerInfo;

public interface ContainerStore {
    void save(String projectId, String containerId);

    ContainerInfo find(String projectId);

    String findProjectId(String containerId);

    void remove(String projectId);

    int size();
}
