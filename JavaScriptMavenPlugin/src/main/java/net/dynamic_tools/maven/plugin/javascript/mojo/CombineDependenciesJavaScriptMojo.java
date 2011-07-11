package net.dynamic_tools.maven.plugin.javascript.mojo;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import net.dynamic_tools.exception.UnableToWriteJSResourcesException;
import net.dynamic_tools.service.*;
import net.dynamic_tools.service.combiner.JSResourceCombiner;
import net.dynamic_tools.service.combiner.JSResourceConcatenatingCombiner;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Combines all the project and dependency javascript into single artifacts.
 *
 * @goal combined-js
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class CombineDependenciesJavaScriptMojo extends AbstractJavaScriptMojo {
	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.withDependencies}" default-value="true"
	 */
	protected boolean withDependencies;

	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.concatenate}" default-value="false"
	 */
	protected boolean concatenate;

	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.concatenate}" default-value="^\\s*\\*+\\s+@requires (\\S*)"
	 */
	protected String jsDependencyRegex;

	/**
	 * Output concatenated javascript
	 *
	 * @parameter expression="${package.minify}" default-value="true"
	 */
	protected boolean minify;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Executing CombineJavaScriptMojo");

		File combinedJavaScriptDirectory = getTargetDirectory("combined-javascript");

		getLog().info("Copy javascript dependency files to " + combinedJavaScriptDirectory);
		copyDependencyFiles("runtime", combinedJavaScriptDirectory);

		getLog().info("Copying project javascript files to " + combinedJavaScriptDirectory);
		copyProjectFiles(combinedJavaScriptDirectory);

		if (withDependencies) {
			getLog().info("Creating javascript artifact with dependencies");
			createJavaScriptArchive("with-dependencies", combinedJavaScriptDirectory);
		}

		File concatenatedJavaScriptFile = new File(projectBuildDirectory, finalName + "-concatenated.js");
		if (concatenatedJavaScriptFile.exists()) {
			deleteFile(concatenatedJavaScriptFile);
		}

		if (concatenate || minify) {
			getLog().info("Creating concatenated javascript file with dependencies");
			writeConcatenatedJavaScriptFile(combinedJavaScriptDirectory, concatenatedJavaScriptFile);
		}

		if (concatenate) {
			getLog().info("Adding concatenated javascript artifact");
			createJavaScriptArchive("concatenated", concatenatedJavaScriptFile);
		}

		if (minify) {
			getLog().info("Adding concatenated javascript artifact");
			Compiler googleCompiler = new Compiler();

			CompilerOptions options = new CompilerOptions();
			// Advanced mode is used here, but additional options could be set, too.
			CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

			JSSourceFile jsSourceFile = JSSourceFile.fromFile(concatenatedJavaScriptFile);
//			JSSourceFile jsSourceFile = JSSourceFile.fromCode("blah.js", "function hello() {\n" + "  alert(\"hello world\");\n" + "}");

			// compile() returns a Result, but it is not needed here.
			googleCompiler.compile(new JSSourceFile[] {}, new JSSourceFile[] {jsSourceFile}, options);

			// The compiler is responsible for generating the compiled code; it is not
			// accessible via the Result.
			String source = googleCompiler.toSource();

			getLog().info(source);
//			createJavaScriptArchive("concatenated", concatenatedJavaScriptFile);
		}

		getLog().info("Finished CombineJavaScriptMojo");
	}

	private void writeConcatenatedJavaScriptFile(File combinedJavaScriptDirectory, File concatenatedJavaScriptFile) throws MojoExecutionException {
		ResourceDependencyManager resourceDependencyManager = new ResourceDependencyManagerImpl();

		JSDependencyReader jsDependencyReader = new JSDependencyReader();
		jsDependencyReader.setPattern(jsDependencyRegex);

		JSResourceLoader jsResourceLoader = new JSResourceLoader();
		jsResourceLoader.setFileFinder(new FileFinder());
		jsResourceLoader.setJsDependencyReader(jsDependencyReader);

		JSDependencyInitialiser jsDependencyInitialiser = new JSDependencyInitialiser();
		jsDependencyInitialiser.setJsResourceLoader(jsResourceLoader);
		jsDependencyInitialiser.setResourceDependencyManager(resourceDependencyManager);
		jsDependencyInitialiser.setPaths(new File[]{combinedJavaScriptDirectory});

		try {
			jsDependencyInitialiser.initialiseDependencies();
			JSResourceCombiner jsResourceCombiner = new JSResourceConcatenatingCombiner();
			jsResourceCombiner.writeJSResourcesToOutputStream(resourceDependencyManager.getAllResources(), new FileOutputStream(concatenatedJavaScriptFile));
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to create concatenated javascript artifact", e);
		} catch (UnableToWriteJSResourcesException e) {
			throw new MojoExecutionException("Unable to create concatenated javascript artifact", e);
		}
	}
}