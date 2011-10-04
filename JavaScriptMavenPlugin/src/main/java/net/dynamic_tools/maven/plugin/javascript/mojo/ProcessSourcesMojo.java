package net.dynamic_tools.maven.plugin.javascript.mojo;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Pulls in all the JavaScript dependencies to a directory in target
 *
 * @goal extract-dependencies
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class ProcessSourcesMojo extends AbstractJavaScriptMojo {
    /**
     * Extract all javascript dependencies to the javaScriptDependencyFolder
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
		getLog().info("Executing ProcessSourcesMojo");

		File testJavaScriptDirectory = getCleanTargetDirectory(javaScriptTestDependencyDirectory);
		File runtimeJavaScriptDirectory = getCleanTargetDirectory(javaScriptRunTimeDependencyDirectory);

		copyDependencyFiles("runtime", runtimeJavaScriptDirectory);
		if (!this.skipTests) {
			copyDependencyFiles("test", testJavaScriptDirectory);
		}

		getLog().info("Finished ProcessSourcesMojo");
    }
}