package com.poc.command;

import com.poc.File;
import com.poc.FileManager;

public class SaveFileCommand implements Command {
    private final FileManager fileManager;
    private final File file;

    SaveFileCommand(FileManager fileManager, File file) {
        this.fileManager = fileManager;
        this.file = file;
    }

    @Override
    public void execute() {
        fileManager.save(file);
    }

    @Override
    public void undo() {
        fileManager.delete(file);
    }
}
