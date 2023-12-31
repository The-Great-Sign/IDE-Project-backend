package goorm.dbjj.ide.lambdahandler.containerstatus.model;

import lombok.Getter;
import lombok.ToString;

@Getter
public class ContainerInfo {

    private final String containerId;
    private ContainerStatus status;

    public ContainerInfo(String containerId) {
        this.containerId = containerId;
        this.status = ContainerStatus.PENDING;
    }

    public void setRunning() {
        this.status = ContainerStatus.RUNNING;
    }

    public void setPending() {
        this.status = ContainerStatus.PENDING;
    }

    public boolean isRunning() {
        return this.status == ContainerStatus.RUNNING;
    }

    public boolean isStopped() {
        return this.status == ContainerStatus.STOPPED;
    }
}
