package goorm.dbjj.ide.lambdahandler.containerstatus;

import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerInfo;

/**
 * 수행중인 컨테이너와 프로젝트 이름을 매핑시켜 저장하는 저장소입니다.
 * 컨테이너 라이프 사이클을 관리하기 위해 사용됩니다.
 * 추후 확장을 위해 인터페이스화 하였습니다.
 */
public interface ContainerStore {

    /**
     * 컨테이너의 정보를 ProjectId와 매핑하여 저장합니다.
     * @param projectId
     * @param containerId
     */
    void save(String projectId, String containerId);

    /**
     * projectId를 통해 실행중인 컨테이너 정보를 획득합니다.
     * @param projectId
     * @return ContainerInfo 객체
     */
    ContainerInfo find(String projectId);

    /**
     * containerId를 통해 projectId를 획득합니다.
     * @param containerId
     * @return
     */
    String findProjectId(String containerId);

    /**
     * 컨테이너가 종료되었을 때 저장소에서 해당 컨테이너 정보를 삭제합니다.
     * @param projectId
     */
    void remove(String projectId);

    /**
     * 현재 저장소에 저장된 컨테이너의 개수를 반환합니다.
     * @return
     */
    int size();
}
