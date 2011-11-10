package net.dynamic_tools.service;

import net.dynamic_tools.exception.CircularDependencyException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceDependencyManager<NamedResource> {
    public void addPaths(File... paths);

    public void removePaths(File... paths);

    public void addMockPaths(File... paths);

    public void removeMockPaths(File... paths);

    public List<NamedResource> getResourcesFor(String... names) throws IOException, CircularDependencyException;

    public List<NamedResource> getResourcesFor(NamedResource... namedResources);

    public List<NamedResource> getTestResourcesFor(String... names) throws IOException, CircularDependencyException;

	public List<NamedResource> getAllResources() throws IOException, CircularDependencyException;

    public boolean contains(String name) throws IOException, CircularDependencyException;
}
