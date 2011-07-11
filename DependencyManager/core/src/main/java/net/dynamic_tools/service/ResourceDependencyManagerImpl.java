package net.dynamic_tools.service;

import net.dynamic_tools.model.NamedResource;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ResourceDependencyManagerImpl<RESOURCE extends NamedResource> implements ResourceDependencyManager<RESOURCE> {

    private SimpleDirectedGraph<RESOURCE, DefaultEdge> dependencyGraph;
    private EdgeReversedGraph<RESOURCE, DefaultEdge> edgeReversedGraph;
    private Map<String, RESOURCE> resourceMap;

    boolean globalDependencyListIsOutOfDate;
    private List<RESOURCE> globalOrderedDependencyList;

    public ResourceDependencyManagerImpl() {
        dependencyGraph = new SimpleDirectedGraph<RESOURCE, DefaultEdge>(DefaultEdge.class);
        edgeReversedGraph = new EdgeReversedGraph<RESOURCE, DefaultEdge>(dependencyGraph);
        resourceMap = new HashMap<String, RESOURCE>();
        globalDependencyListIsOutOfDate = true;
    }

    public void addResource(RESOURCE namedResource) {
        if (this.contains(namedResource)) {
            throw new IllegalArgumentException("The supplied resource already exists.");
        }
        dependencyGraph.addVertex(namedResource);
        resourceMap.put(namedResource.getName(), namedResource);
        globalDependencyListIsOutOfDate = true;
    }

    public void addDependency(RESOURCE resource, RESOURCE dependency) {
        if (!this.contains(resource)) {
            this.addResource(resource);
        }

        if (!this.contains(dependency)) {
            this.addResource(dependency);
        }

        dependencyGraph.addEdge(dependency, resource);
        globalDependencyListIsOutOfDate = true;
    }

    public RESOURCE getResourceByName(String name) {
        return resourceMap.get(name);
    }

    public List<RESOURCE> getResourcesFor(String name) {
        RESOURCE startResource = getResourceByName(name);
        return getResourcesFor(startResource);
    }

    public List<RESOURCE> getResourcesFor(RESOURCE resource) {

        if (globalDependencyListIsOutOfDate) {
            this.rebuildOrderedDependencyList();
        }

        List<RESOURCE> resourceList = new ArrayList<RESOURCE>();

        BreadthFirstIterator<RESOURCE, DefaultEdge> orderIterator = new BreadthFirstIterator<RESOURCE, DefaultEdge>(edgeReversedGraph, resource);
        while (orderIterator.hasNext()) {
            resourceList.add(orderIterator.next());
        }

        List<RESOURCE> orderedDependencyList = new ArrayList<RESOURCE>(globalOrderedDependencyList);
        orderedDependencyList.retainAll(resourceList);

        return orderedDependencyList;
    }

	public List<RESOURCE> getAllResources() {
		if (globalDependencyListIsOutOfDate) {
            this.rebuildOrderedDependencyList();
        }
		return new ArrayList<RESOURCE>(globalOrderedDependencyList);
	}

	private void rebuildOrderedDependencyList() {
        TopologicalOrderIterator<RESOURCE, DefaultEdge> orderIterator = new TopologicalOrderIterator<RESOURCE, DefaultEdge>(dependencyGraph);
        globalOrderedDependencyList = new ArrayList<RESOURCE>();
        while (orderIterator.hasNext()) {
            globalOrderedDependencyList.add(orderIterator.next());
        }
        globalDependencyListIsOutOfDate = false;
    }

    public boolean contains(String name) {
        return resourceMap.containsKey(name);
    }

    public boolean contains(RESOURCE namedResource) {
        return resourceMap.containsValue(namedResource);
    }
}
