package net.dynamic_tools.maven.plugin.javascript.mojo;

import net.dynamic_tools.exception.CircularDependencyException;
import net.dynamic_tools.exception.UnableToWriteJSResourcesException;
import net.dynamic_tools.model.JSResource;
import net.dynamic_tools.service.*;
import net.dynamic_tools.service.combiner.JSResourceCombiner;
import net.dynamic_tools.service.combiner.JSResourceConcatenatingCombiner;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Combines all the project and dependency javascript into single artifacts.
 *
 * @goal concatenate-js
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class ConcatenateJavaScriptMojo extends AbstractJavaScriptMojo {
	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.jsDependencyRegex}" default-value="^\\s*\\*+\\s+@requires (\\S*)"
	 */
	protected String jsDependencyRegex;

	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.jsDependencyRegexGroupNumber}" default-value="1"
	 */
	protected int jsDependencyRegexGroupNumber;

	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.cocatenatedFileName}" default-value="${project.build.finalName}-concatenated.js"
	 */
	protected String concatenatedFileName;

	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.includes}"
	 */
	protected List<String> includes;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Executing ConcatenateJavaScriptMojo");

		File concatenatedJavaScriptFile = getCleanTargetFile(concatenatedFileName);

		JSResourceDependencyManagerImpl jsResourceDependencyManager = new JSResourceDependencyManagerImpl();

		JSDependencyReader jsDependencyReader = new JSDependencyReader();
		jsDependencyReader.setPattern(jsDependencyRegex, jsDependencyRegexGroupNumber);

        jsResourceDependencyManager.setJsDependencyReader(jsDependencyReader);
        jsResourceDependencyManager.setFileFinder(new FileFinder());

		if (javaScriptDirectoryExists()) {
			jsResourceDependencyManager.addPaths(getProjectDirectory(javaScriptDirectory));
		}

        jsResourceDependencyManager.addPaths(getTargetDirectory(javaScriptRunTimeDependencyDirectory + File.separator + "javascript"));

		try {
			JSResourceCombiner jsResourceCombiner = new JSResourceConcatenatingCombiner();
			List<JSResource> resources = jsResourceDependencyManager.getResourcesFor(getProjectResources(jsResourceDependencyManager.getAllResources()));
			getLog().info("Writing concatenated javascript to " + concatenatedJavaScriptFile);
			jsResourceCombiner.writeJSResourcesToOutputStream(resources, new FileOutputStream(concatenatedJavaScriptFile));
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to create concatenated javascript artifact", e);
		} catch (UnableToWriteJSResourcesException e) {
			throw new MojoExecutionException("Unable to create concatenated javascript artifact", e);
		} catch (CircularDependencyException e) {
            throw new MojoExecutionException("Unable to create concatenated javascript artifact", e);
        }

        getLog().info("Finished ConcatenateJavaScriptMojo");
	}

	private List<JSResource> getProjectResources(List<JSResource> allResources) {
		List<JSResource> projectResources = new ArrayList<JSResource>();
		for (JSResource jsResource : allResources) {
			if (isIncluded(jsResource)) {
				projectResources.add(jsResource);
			}
		}
		return projectResources;
	}

	private boolean isIncluded(JSResource jsResource) {
		String fileName = jsResource.getJsResourceFile().toString();
		if (fileName.contains(javaScriptDirectory)) {
			return true;
		}
		for (String include : includes) {
			include = include.replaceAll("^[*/\\\\]+", "").replaceAll("[\\/]", File.separatorChar == '\\' ? "\\\\" : File.separator);
			if (fileName.endsWith(include)) {
				return true;
			}
		}
		return false;
	}
}