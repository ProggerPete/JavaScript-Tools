package net.dynamic_tools.service;

import net.dynamic_tools.model.NamedResource;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceDependencyManager<RESOURCE extends NamedResource> {
    public void addResource(RESOURCE namedResource);

    public void addDependency(RESOURCE namedResource, RESOURCE dependency);

    public RESOURCE getResourceByName(String name);

    public List<RESOURCE> getResourcesFor(String name);

    public List<RESOURCE> getResourcesFor(RESOURCE namedResource);

	public List<RESOURCE> getAllResources();

    public boolean contains(String name);

    public boolean contains(RESOURCE namedResource);
}
