package net.dynamic_tools.maven.plugin.javascript.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Echos an object string to the output screen.
 *
 * @goal testCompile
 * @phase testCompile
 * @requiresDependencyResolution test
 */
public class TestCompileMojo extends AbstractJavaScriptMojo {
	/**
	 * The directory test sources will be extracted to.
	 *
	 * @parameter expression="${package.testScriptsDirectory}" default-value="test-javascript"
	 */
	private String testScriptsDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Executing TestCompileMojo");

		File javascriptTestDirectory = getCleanTargetDirectory(testScriptsDirectory);

		getLog().info("Copy javascript dependency files to " + javascriptTestDirectory);
		copyDependencyFiles("test", javascriptTestDirectory);

		getLog().info("Copying project javascript files to " + javascriptTestDirectory);
		copyProjectFiles(javascriptTestDirectory);

		getLog().info("Finished TestCompileMojo");
	}
}