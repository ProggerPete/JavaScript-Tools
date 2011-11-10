package net.dynamic_tools.service;

import net.dynamic_tools.exception.CircularDependencyException;
import net.dynamic_tools.model.JSResource;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResourceDependencyManagerImplTest {
    JSResourceDependencyManagerImpl resourceDependencyManager;
    private Map<String, File> files = new HashMap<String, File>();
    private Map<String, File> mockFiles = new HashMap<String, File>();
    private MockFileCreator normalFileCreator;
    private MockFileCreator mockFileCreator;

    @Before
    public void setup() throws IOException {
        resourceDependencyManager = new JSResourceDependencyManagerImpl();
        FileFinder fileFinder = mock(FileFinder.class);
        JSDependencyReader jsDependencyReader = mock(JSDependencyReader.class);

        File normalPath = mock(File.class);
        File mockPath = mock(File.class);
        List<File> normalPaths = Arrays.asList(normalPath);
        List<File> mockPaths = Arrays.asList(mockPath);

        normalFileCreator = new MockFileCreator(normalPath, normalPaths, files, fileFinder, jsDependencyReader);
        mockFileCreator = new MockFileCreator(mockPath, mockPaths, mockFiles, fileFinder, jsDependencyReader);

        normalFileCreator.create("a", "b", "d");
        normalFileCreator.create("b", "c", "e");
        normalFileCreator.create("c", "d", "f");
        normalFileCreator.create("d", "f");
        normalFileCreator.create("e");
        normalFileCreator.create("f");
        normalFileCreator.create("g", "e");

        mockFileCreator.create("b");
        mockFileCreator.create("d");

        when(fileFinder.getFile("a.js", Arrays.asList(mockPath, normalPath))).thenReturn(files.get("a"));
        when(fileFinder.getFile("b.js", Arrays.asList(mockPath, normalPath))).thenReturn(mockFiles.get("b"));
        when(fileFinder.getFile("c.js", Arrays.asList(mockPath, normalPath))).thenReturn(files.get("c"));
        when(fileFinder.getFile("d.js", Arrays.asList(mockPath, normalPath))).thenReturn(mockFiles.get("d"));
        when(fileFinder.getFile("e.js", Arrays.asList(mockPath, normalPath))).thenReturn(files.get("e"));
        when(fileFinder.getFile("f.js", Arrays.asList(mockPath, normalPath))).thenReturn(files.get("f"));
        when(fileFinder.getFile("g.js", Arrays.asList(mockPath, normalPath))).thenReturn(files.get("g"));

        resourceDependencyManager.setFileFinder(fileFinder);
        resourceDependencyManager.setJsDependencyReader(jsDependencyReader);
        resourceDependencyManager.addPaths(normalPath);
        resourceDependencyManager.addMockPaths(mockPath);
    }

    @Test
    public void noDependencies() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getResourcesFor("f");
        List<File> expectation = Arrays.asList(
                files.get("f")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test
    public void singleDependency() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getResourcesFor("d");
        List<File> expectation = Arrays.asList(
                files.get("f"),
                files.get("d")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test
    public void multipleStartPoints() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getResourcesFor("d", "g");
        List<File> expectation = Arrays.asList(
                files.get("e"),
                files.get("f"),
                files.get("d"),
                files.get("g")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));

        resources = resourceDependencyManager.getResourcesFor("b", "g");
        expectation = Arrays.asList(
                files.get("e"),
                files.get("f"),
                files.get("d"),
                files.get("g"),
                files.get("c"),
                files.get("b")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test
    public void getAllResources() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getAllResources();

        List<File> expectation = Arrays.asList(
                files.get("e"),
                files.get("f"),
                files.get("d"),
                files.get("g"),
                files.get("c"),
                files.get("b"),
                files.get("a")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test
    public void multiplePathsToDependency() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getResourcesFor("a");
        List<File> expectation = Arrays.asList(
                files.get("e"),
                files.get("f"),
                files.get("d"),
                files.get("c"),
                files.get("b"),
                files.get("a")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));

        resources = resourceDependencyManager.getResourcesFor("b");

        expectation = Arrays.asList(
                files.get("e"),
                files.get("f"),
                files.get("d"),
                files.get("c"),
                files.get("b")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));

        resources = resourceDependencyManager.getResourcesFor("c");
        expectation = Arrays.asList(
                files.get("f"),
                files.get("d"),
                files.get("c")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test
    public void mockDependenciesRetrievedForTestResources() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getTestResourcesFor("a");

        List<File> expectation = Arrays.asList(
                mockFiles.get("b"),
                mockFiles.get("d"),
                files.get("a")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test
    public void mockDependenciesAreNotUsedForTheSpecifiedTestResourceButAreForDependencies() throws IOException, CircularDependencyException {
        List<JSResource> resources = resourceDependencyManager.getTestResourcesFor("b");

        List<File> expectation = Arrays.asList(
                mockFiles.get("d"),
                files.get("e"),
                files.get("f"),
                files.get("c"),
                files.get("b")
        );

        assertEquals(expectation, extractFileListFromJSResourceList(resources));
    }

    @Test(expected=CircularDependencyException.class)
    public void circularDependenciesCauseAnExceptionWhenGettingAllResource() throws IOException, CircularDependencyException {
        normalFileCreator.create("x", "y");
        normalFileCreator.create("y", "z");
        normalFileCreator.create("z", "x");
        resourceDependencyManager.getAllResources();
    }

    @Test(expected=CircularDependencyException.class)
    public void circularDependenciesCauseAnExceptionWhenGettingASpecificResource() throws IOException, CircularDependencyException {
        normalFileCreator.create("x", "y");
        normalFileCreator.create("y", "z");
        normalFileCreator.create("z", "x");
        resourceDependencyManager.getResourcesFor("x");
    }

    private List<File> extractFileListFromJSResourceList(List<JSResource> jsResources) {
        List<File> files = new ArrayList<File>();
        for (JSResource jsResource : jsResources) {
            files.add(jsResource.getJsResourceFile());
        }
        return files;
    }

    private class MockFileCreator {
        private File path;
        private List<File> paths;
        private Map<String, File> fileMap;
        private FileFinder fileFinder;
        private JSDependencyReader jsDependencyReader;

        private MockFileCreator(File path, List<File> paths, Map<String, File> fileMap, FileFinder fileFinder, JSDependencyReader jsDependencyReader) {
            this.path = path;
            this.paths = paths;
            this.fileMap = fileMap;
            this.fileFinder = fileFinder;
            this.jsDependencyReader = jsDependencyReader;
        }

        public File create(String name, String... dependencies) throws IOException {
            File mockFile = mock(File.class);
            when(mockFile.getName()).thenReturn(name + ".js");
            when(mockFile.getParentFile()).thenReturn(path);
            when(fileFinder.getFile(name + ".js", paths)).thenReturn(mockFile);
            fileMap.put(name, mockFile);
            when(jsDependencyReader.readDependencies(mockFile)).thenReturn(new HashSet<String>(Arrays.asList(dependencies)));
            when(fileFinder.getAllFilesWithExtension(path, ".js")).thenReturn(new ArrayList<File>(fileMap.values()));
            return mockFile;
        }
    }
}
