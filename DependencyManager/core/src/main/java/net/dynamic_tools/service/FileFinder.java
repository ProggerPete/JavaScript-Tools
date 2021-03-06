package net.dynamic_tools.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class FileFinder {
    public File getFile(String fileName, List<File> rootPaths) {
        for (File rootPath : rootPaths) {
            File resourceFile = new File(rootPath, fileName);
            if (resourceFile.exists()) {
                return resourceFile;
            }
        }
        return null;
    }

    public List<File> getAllFilesWithExtension(File root, String extension) {
        List<File> fileList = new ArrayList<File>();
        addFiles(fileList, root, extension);
        return fileList;
    }

    private void addFiles(List<File> fileList, File file, String extension) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                addFiles(fileList, child, extension);
            }
        }
        else {
            if (file.getName().endsWith(extension)) {
                fileList.add(file);
            }
        }
    }
}
