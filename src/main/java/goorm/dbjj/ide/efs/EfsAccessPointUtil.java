package goorm.dbjj.ide.efs;

public interface EfsAccessPointUtil {
    String generateAccessPoint(String projectId);

    void deleteAccessPoint(String accessPointId);
}
