package net.dynamic_tools.service;

import net.dynamic_tools.model.JSResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/13/11
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class JSDependencyInitialiser {
	@Autowired
	JSResourceLoader jsResourceLoader;

	@Autowired
	ResourceDependencyManager<JSResource> resourceDependencyManager;

	private File[] files;

	@Value("${jsResources.path}")
	public void setPaths(Resource... springResources) throws IOException {
		files = new File[springResources.length];
		for (int i=0; i < files.length; i++) {
			files[i] = springResources[i].getFile();
		}
	}

	public void setPaths(File... paths) {
		files = paths;
	}

//	public void setPaths(String... jsPaths) throws IOException {
//		files = new File[jsPaths.length];
//		for (int i=0; i < files.length; i++) {
//			files[i] = new File(jsPaths[i]);
//		}
//	}

	@PostConstruct
	public void initialiseDependencies() throws IOException {
		jsResourceLoader.loadResourcesFromPaths(resourceDependencyManager, files);
	}

	public void setResourceDependencyManager(ResourceDependencyManager<JSResource> resourceDependencyManager) {
		this.resourceDependencyManager = resourceDependencyManager;
	}

	public void setJsResourceLoader(JSResourceLoader jsResourceLoader) {
		this.jsResourceLoader = jsResourceLoader;
	}
}
