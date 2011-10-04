package net.dynamic_tools.model;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class NamedResource<DEPENDENCY_CLASS extends NamedResource> {
    private String name;
	protected final Set<DEPENDENCY_CLASS> dependencies = new HashSet<DEPENDENCY_CLASS>();

    public NamedResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

	public boolean isDependentOn(DEPENDENCY_CLASS resource) {
        for (DEPENDENCY_CLASS dependency : dependencies) {
            if (dependency.equals(resource) || dependency.isDependentOn(resource)) {
                return true;
            }
        }
        return false;
    }

	public void addDependency(DEPENDENCY_CLASS dependency) {
        if (dependency.isDependentOn(this)) {
            throw new InvalidParameterException("Circular dependencies are not supported.");
        }
        dependencies.add(dependency);
    }

	public Set<DEPENDENCY_CLASS> getDependencies() {
        return new HashSet<DEPENDENCY_CLASS>(dependencies);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && name.equals(((NamedResource) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
