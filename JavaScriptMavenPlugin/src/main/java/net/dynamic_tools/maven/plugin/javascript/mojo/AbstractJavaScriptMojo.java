package net.dynamic_tools.maven.plugin.javascript.mojo;

import net.dynamic_tools.maven.plugin.javascript.Types;
import net.dynamic_tools.maven.plugin.javascript.util.JavaScriptArchiver;
import net.dynamic_tools.maven.plugin.javascript.util.JavaScriptArtifactManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 22/06/11
 * Time: 10:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractJavaScriptMojo extends AbstractMojo {
	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The maven project.
	 *
	 * @parameter expression="${skipTests}" default-value="false"
	 * @required
	 * @readonly
	 */
	protected boolean skipTests;

	/**
     * @component
     */
    protected MavenProjectHelper mavenProjectHelper;

	/**
     * The final name for the project
     *
     * @parameter default-value="${project.build.finalName}"
     */
    protected String finalName;

	/**
	 * @parameter expression="${project.build.directory}"
	 * @required
	 * @readonly
	 */
	protected File projectBuildDirectory;

	/**
	 * The directory the javascript source is in
	 *
	 * @parameter expression="${package.javaScriptDirectory}" default-value="src/main/javascript"
	 */
	protected String javaScriptDirectory;

	/**
	 * The JavaScript dependency folder
	 *
	 * @parameter expression="${package.jsRunTimeDependencyOutputDirectory}" default-value="javaScriptDependencies/runtime"
	 */
	protected String javaScriptRunTimeDependencyDirectory;

	/**
	 * The JavaScript dependency folder
	 *
	 * @parameter expression="${package.jsTestDependencyOutputDirectory}" default-value="javaScriptDependencies/test"
	 */
	protected String javaScriptTestDependencyDirectory;

	/**
	 * @parameter
	 */
	protected File manifest;

	/**
	 * Plexus archiver.
	 *
	 * @component role="org.codehaus.plexus.archiver.Archiver" role-hint="javascript"
	 * @required
	 */
	private JavaScriptArchiver archiver;

	/**
	 * @component
	 */
	private JavaScriptArtifactManager javaScriptArtifactManager;

	protected void copyDependencyFiles(String scope, File targetDirectory) throws MojoExecutionException {
		try {
			javaScriptArtifactManager.unpack(project, "test", targetDirectory, false);
		} catch (ArchiverException e) {
			throw new MojoExecutionException("Unable to extract javascript archive", e);
		}
	}

	protected void copyProjectFiles(File targetDirectory) throws MojoExecutionException {
		File projectJavaScriptDirectory = new File(project.getBasedir(), javaScriptDirectory);
		if (!projectJavaScriptDirectory.exists()) {
			getLog().info("No project javascript found at " + projectJavaScriptDirectory.toString());
		}
		else {
            copyDirectoryStructure(targetDirectory, projectJavaScriptDirectory);
        }
	}

    protected void copyDirectoryStructure(File targetDirectory, File projectJavaScriptDirectory) throws MojoExecutionException {
        try {
            FileUtils.copyDirectoryStructure(projectJavaScriptDirectory, targetDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy files", e);
        }
    }

    protected void createJavaScriptArchive(String classifier, File sourceFile) throws MojoExecutionException {
		createJavaScriptArchive(classifier, new File[] {sourceFile});
	}

	protected void createJavaScriptArchive(File sourceFiles) throws MojoExecutionException {
		createJavaScriptArchive(null, new File[] {sourceFiles});
	}

	protected void createJavaScriptArchive(File[] sourceFiles) throws MojoExecutionException {
		createJavaScriptArchive(null, sourceFiles);
	}

	protected void createJavaScriptArchive(String classifier, File[] sourceFiles) throws MojoExecutionException {
		String archiveName = (classifier == null ? finalName : finalName + "-" + classifier) + "." + Types.JAVASCRIPT_EXTENSION;
		File jsArchive = new File(projectBuildDirectory, archiveName);
		try {
			JavaScriptArchiver javaScriptArchiver = new JavaScriptArchiver();
			if (manifest != null) {
				javaScriptArchiver.setManifest(manifest);
			}
			else {
				javaScriptArchiver.createDefaultManifest(project);
			}
			for (int i=0; i < sourceFiles.length; i++) {
				if (sourceFiles[i].isDirectory()) {
					javaScriptArchiver.addDirectory(sourceFiles[i]);
				}
				else {
					javaScriptArchiver.addFile(sourceFiles[i], sourceFiles[i].getName());
				}
			}

			javaScriptArchiver.addFile(project.getFile(), "META-INF/maven/" + project.getGroupId() + "/" + project.getArtifactId() + "/pom.xml");
			javaScriptArchiver.setDestFile(jsArchive);
			javaScriptArchiver.createArchive();
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to create the javascript archive", e);
		}

		if (classifier != null) {
			mavenProjectHelper.attachArtifact(project, Types.JAVASCRIPT_TYPE, classifier, jsArchive);
		}
		else {
			project.getArtifact().setFile(jsArchive);
		}
	}

	/**
	 * Sometimes file deletion fails the 1st time but succeeds later, not sure why.
	 *
	 * @param fileToDelete
	 * @throws MojoExecutionException
	 */
	protected void deleteFile(File fileToDelete) throws MojoExecutionException {
		int maxRetries = 10;
		while (fileToDelete.exists()) {
			if (fileToDelete.delete() && maxRetries-- <= 0) {
				throw new MojoExecutionException("Unable to delete old concatenated javascript file");
			}
		}
	}

	protected File getProjectDirectory(String directoryName) throws MojoExecutionException {
		return new File(project.getBasedir(), directoryName);
	}

	protected File getTargetDirectory(String directoryName) throws MojoExecutionException {
		return new File(projectBuildDirectory, directoryName);
	}

	protected File getCleanTargetDirectory(String directoryName) throws MojoExecutionException {
		File targetDirectory = getTargetDirectory(directoryName);
		if (targetDirectory.exists()) {
			try {
				FileUtils.cleanDirectory(targetDirectory);
			} catch (IOException e) {
				throw new MojoExecutionException("Unable to clean old javascript directory - " + targetDirectory.toString(), e);
			}
		}
		else if (!targetDirectory.mkdirs()) {
			throw new MojoExecutionException("Unable to create javascript directory - " + targetDirectory.toString());
		}
		return targetDirectory;
	}

	protected File getCleanTargetFile(String fileName) throws MojoExecutionException {
		File targetFile = new File(projectBuildDirectory, fileName);
		int maxRetries = 10;
		while (targetFile.exists()) {
			if (targetFile.delete() && maxRetries-- <= 0) {
				throw new MojoExecutionException("Unable to delete old concatenated javascript file");
			}
		}
		targetFile.getParentFile().mkdirs();
		return targetFile;
	}

	public boolean javaScriptDirectoryExists() throws MojoExecutionException {
		return getProjectDirectory(javaScriptDirectory).exists();
	}
}
