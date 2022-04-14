package com.nahorny.solution3rethinking;

import java.io.File;

public class FileAndDepthStorage {
    private final File file;
    private final int depth;

    public FileAndDepthStorage(File file, int depth) {
        this.file = file;
        this.depth = depth;
    }

    public File getFile() {
        return file;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof FileAndDepthStorage)
                && this.file.equals(((FileAndDepthStorage) other).file)
                && this.depth == ((FileAndDepthStorage) other).depth;
    }
}
