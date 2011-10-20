package net.dynamic_tools.service;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/12/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSDependencyReaderTest {
	private File jsFilesLocation;
	private JSDependencyReader jsDependencyReader;

	@Before
    public void setup() throws UnsupportedEncodingException {
        URL jsFilesURL = this.getClass().getClassLoader().getResource("jsFiles");
        jsFilesLocation = new File(URLDecoder.decode(jsFilesURL.getFile(), "UTF-8"));

		jsDependencyReader = new JSDependencyReader();
		jsDependencyReader.setPattern("^\\s*//\\s*import (\\S+)");

    }

	@Test
	public void testReadDependencies() throws IOException {
		Set<String> dependencies = jsDependencyReader.readDependencies(new File(jsFilesLocation, "com/alpha/ClassA.js"));
		assertTrue(dependencies.contains("com.beta.ClassD"));
		assertTrue(dependencies.contains("com.alpha.ClassB"));

		dependencies = jsDependencyReader.readDependencies(new File(jsFilesLocation, "com/gamma/ClassF.js"));
		assertEquals(0, dependencies.size());
	}
}
