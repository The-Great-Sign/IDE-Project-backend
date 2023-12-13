package goorm.dbjj.ide.service;

import goorm.dbjj.ide.api.exception.ResourceNotFoundException;
import goorm.dbjj.ide.storageManager.StorageManager;

public class StorageService {
    private final StorageManager storageManager;

    public StorageService(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void a() {
        try {
            storageManager.createDirectory("");
        } catch (ResourceNotFoundException e) {

        }

    }
}
