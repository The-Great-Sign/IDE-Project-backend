package goorm.dbjj.ide.model.type;

public enum FileType {
    PYTHON("py"),
    UNKNOWN("---");

    private final String extension; // 확장자

    FileType(String extension) {
        this.extension = extension;
    }

    public static FileType of(String extension) {
        for (FileType fileType : values()) {
            if(fileType.extension.equals(extension)) {
                return fileType;
            }
        }
        return UNKNOWN;
    }
}
