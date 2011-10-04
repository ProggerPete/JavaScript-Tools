package net.dynamic_tools.service;

import net.dynamic_tools.model.NamedResource;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.stereotype.Component;

import java.util.*;

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

	/**
	 * Specify a dependy
	 *
	 * @param resource the resource that has the dependency
	 * @param dependency the dependency resource
	 */
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

	public List<RESOURCE> getResourcesFor(String... names) {
		List<RESOURCE> resources = new ArrayList<RESOURCE>();
		for (String name : names) {
			resources.add(getResourceByName(name));
		}
        return getResourcesFor(resources);
    }

	@Override
	public List<RESOURCE> getResourcesFor(RESOURCE namedResource) {
		return getResourcesFor(Arrays.asList(namedResource));
	}

	public List<RESOURCE> getResourcesFor(List<RESOURCE> resources) {

        if (globalDependencyListIsOutOfDate) {
            this.rebuildOrderedDependencyList();
        }

		List<RESOURCE> resourceStartPoints = simplifyResourceList(resources);
		Set<RESOURCE> resourceSet = new HashSet<RESOURCE>(resourceStartPoints);
		for (RESOURCE resource : resourceStartPoints) {
			BreadthFirstIterator<RESOURCE, DefaultEdge> orderIterator = new BreadthFirstIterator<RESOURCE, DefaultEdge>(edgeReversedGraph, resource);
			while (orderIterator.hasNext()) {
				resourceSet.add(orderIterator.next());
			}
		}

        List<RESOURCE> orderedDependencyList = new ArrayList<RESOURCE>(globalOrderedDependencyList);
        orderedDependencyList.retainAll(resourceSet);

        return orderedDependencyList;
    }

	/**
	 * Return a filtered list of resources, removing resources that are depended on by other resources.
	 *
	 * @param resources the resources to simplify
	 * @return the filtered list of resources
	 */
	private List<RESOURCE> simplifyResourceList(List<RESOURCE> resources) {
		List<RESOURCE> resourceList = new ArrayList<RESOURCE>();

		for (int i = 0; i < resources.size(); i++) {
			boolean isDependedOn = false;
			RESOURCE resourceOne = resources.get(i);
			for (int j = i+1; j < resources.size(); j++) {
				RESOURCE resourceTwo = resources.get(j);
				if (resourceTwo.isDependentOn(resourceOne)) {
					isDependedOn = true;
					break;
				}
			}
			if (!isDependedOn) {
				resourceList.add(resourceOne);
			}
		}
		return resourceList;
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
