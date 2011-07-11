package net.dynamic_tools.service;

import net.dynamic_tools.model.JSResource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/12/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResourceLoaderTest {

	@Test
    public void testLoad() throws IOException {
        URL jsFilesURL = this.getClass().getClassLoader().getResource("jsFiles");
		File jsFilesLocation = new File(URLDecoder.decode(jsFilesURL.getFile(), "UTF-8"));

		JSResourceLoader jsResourceLoader = new JSResourceLoader();

		jsResourceLoader.setFileFinder(new FileFinder());
		JSDependencyReader jsDependencyReader = new JSDependencyReader();
		jsDependencyReader.setPattern("^\\s*//\\s*import (\\S+)");
		jsResourceLoader.setJsDependencyReader(jsDependencyReader);

		ResourceDependencyManager<JSResource> resourceDependencyManager = new ResourceDependencyManagerImpl<JSResource>();
		jsResourceLoader.loadResourcesFromPaths(resourceDependencyManager, jsFilesLocation);

		List<JSResource> allResources = resourceDependencyManager.getAllResources();
		assertEquals(6, allResources.size());

		assertTrue(allResources.get(0).getName().equals("com.gamma.ClassE") || allResources.get(0).getName().equals("com.gamma.ClassF"));
        assertTrue(allResources.get(1).getName().equals("com.gamma.ClassE") || allResources.get(1).getName().equals("com.gamma.ClassF"));
        assertEquals("com.beta.ClassD", allResources.get(2).getName());
        assertEquals("com.beta.ClassC", allResources.get(3).getName());
        assertEquals("com.alpha.ClassB", allResources.get(4).getName());
        assertEquals("com.alpha.ClassA", allResources.get(5).getName());
    }
}
