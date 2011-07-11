package net.dynamic_tools.service;

import net.dynamic_tools.model.JSResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class JSResourceLoader {
	@Autowired
	FileFinder fileFinder;

	@Autowired
	JSDependencyReader jsDependencyReader;

	public void loadResourcesFromPaths(ResourceDependencyManager<JSResource> resourceDependencyManager, File... paths) throws IOException {
		for (File path : paths) {
			loadResourcesFromPath(resourceDependencyManager, path);
		}
	}

	private void loadResourcesFromPath(ResourceDependencyManager<JSResource> resourceDependencyManager, File path) throws IOException {
		if (!path.isDirectory()) {
			throw new IllegalArgumentException("path must be a directory");
		}
		List<File> jsFiles = fileFinder.getAllFilesWithExtension(path, ".js");
		Map<JSResource, Set<String>> dependencyMap = new HashMap<JSResource, Set<String>>();
		for (File jsFile : jsFiles) {
			JSResource jsResource = new JSResource(jsFile, getNameFromLocation(path, jsFile));
			resourceDependencyManager.addResource(jsResource);

			dependencyMap.put(jsResource, jsDependencyReader.readDependencies(jsFile));
		}

		for (JSResource jsResource : dependencyMap.keySet()) {
			Set<String> dependencies = dependencyMap.get(jsResource);
			for (String dependencyName : dependencies) {
				JSResource dependency = resourceDependencyManager.getResourceByName(dependencyName);
				if (dependency == null) {
					throw new IOException("Unable to resolve dependency of '" + jsResource.getName() + "' on '" + dependencyName + "'");
				}
				resourceDependencyManager.addDependency(jsResource, dependency);
			}
		}
	}

	public String getNameFromLocation(File rootPath, File jsFile) {
		String jsFileName = jsFile.getName();
		String name = jsFileName.substring(0, jsFileName.length() - 3);
		File parent = jsFile.getParentFile();
		while (!parent.equals(rootPath)) {
			name = parent.getName() + "." + name;
			parent = parent.getParentFile();
		}
		return name;
	}


	public void setFileFinder(FileFinder fileFinder) {
		this.fileFinder = fileFinder;
	}

	public void setJsDependencyReader(JSDependencyReader jsDependencyReader) {
		this.jsDependencyReader = jsDependencyReader;
	}
}
