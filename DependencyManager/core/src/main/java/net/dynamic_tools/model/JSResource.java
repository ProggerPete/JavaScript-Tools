package net.dynamic_tools.model;

import java.io.File;
import java.net.URI;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/9/11
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResource extends NamedResource {
    private File jsResourceFile;
    private final Set<JSResource> dependencies;

    public JSResource(File jsResourceFile, String fullName) {
        super(fullName);
        if (fullName == null || jsResourceFile == null) {
            throw new InvalidParameterException("You must specify the full name and file for the resource.");
        }
        this.jsResourceFile = jsResourceFile;
        dependencies = new HashSet<JSResource>();
    }

       public Set<JSResource> getDependencies() {
        return new HashSet<JSResource>(dependencies);
    }

    public void addDependency(JSResource dependency) {
        if (dependency.isDependentOn(this)) {
            throw new InvalidParameterException("Circular dependencies are not supported.");
        }
        dependencies.add(dependency);
    }

    public boolean isDependentOn(JSResource jsResource) {
        for (JSResource jsDependency : dependencies) {
            if (jsDependency.equals(jsResource) || jsDependency.isDependentOn(jsResource)) {
                return true;
            }
        }
        return false;
    }

    public File getJsResourceFile() {
        return jsResourceFile;
    }

	public URI getJsResourceURI() {
        return jsResourceFile.toURI();
    }
}
