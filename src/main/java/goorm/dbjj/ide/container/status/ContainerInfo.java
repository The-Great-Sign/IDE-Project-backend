package goorm.dbjj.ide.container.status;

import lombok.Getter;

@Getter
public class ContainerInfo {

    private enum Status {
        STOPPED,PENDING,RUNNING
    }

    private final String containerId;
    private Status status;

    public ContainerInfo(String containerId) {
        this.containerId = containerId;
        this.status = Status.STOPPED;
    }

    public void setRunning() {
        this.status = Status.RUNNING;
    }

    public void setPending() {
        this.status = Status.PENDING;
    }

    public boolean isRunning() {
        return this.status == Status.RUNNING;
    }

    public boolean isStopped() {
        return this.status == Status.STOPPED;
    }
}
