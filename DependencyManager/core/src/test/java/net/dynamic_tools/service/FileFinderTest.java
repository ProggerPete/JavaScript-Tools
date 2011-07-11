package net.dynamic_tools.service;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/12/11
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileFinderTest {
	@Test
	public void testGetAllFilesWithExtension() throws Exception {
		URL jsFilesURL = this.getClass().getClassLoader().getResource("jsFiles");
		File jsFilesLocation = new File(URLDecoder.decode(jsFilesURL.getFile(), "UTF-8"));

		List<File> expectedFiles = Arrays.asList(
				new File(jsFilesLocation, "com/alpha/ClassA.js"),
				new File(jsFilesLocation, "com/alpha/ClassB.js"),
				new File(jsFilesLocation, "com/beta/ClassC.js"),
				new File(jsFilesLocation, "com/beta/ClassD.js"),
				new File(jsFilesLocation, "com/gamma/ClassE.js"),
				new File(jsFilesLocation, "com/gamma/ClassF.js")
		);

		FileFinder fileFinder = new FileFinder();

		List<File> jsFiles = fileFinder.getAllFilesWithExtension(jsFilesLocation, ".js");

		assertEquals(6, jsFiles.size());
		assertTrue(jsFiles.containsAll(expectedFiles));
	}
}
