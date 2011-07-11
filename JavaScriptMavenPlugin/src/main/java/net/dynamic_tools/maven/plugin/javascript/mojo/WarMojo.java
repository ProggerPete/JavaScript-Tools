package net.dynamic_tools.maven.plugin.javascript.mojo;

import net.dynamic_tools.maven.plugin.javascript.util.JavaScriptArtifactManager;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Echos an object string to the output screen.
 *
 * @goal war-package
 * @phase compile
 * @requiresDependencyResolution test
 */
public class WarMojo extends AbstractMojo {
    /**
     * @component
     */
    MavenProjectHelper projectHelper;

    /**
     * Map of of plugin artifacts.
     *
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    protected Map pluginArtifactMap;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File projectBuildDirectory;

    /**
     * @component
     */
    protected JavaScriptArtifactManager javaScriptArtifactManager;

    /**
     * The filename of the js file.
     *
     * @parameter default-value="${project.build.finalName}"
     */
    private String finalName;

    /**
     * Any Object to print out.
     * @parameter expression="${package.javaScriptDirectory}" default-value="javascript"
     */
    private String scriptsDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting WarMojo");

//        project.getArtifactMap()
        List dependencies = new ArrayList(project.getArtifacts());

        File warDirectory = new File(projectBuildDirectory, finalName + "/" + scriptsDirectory);
        if (!warDirectory.exists()) {
            if (!warDirectory.mkdirs()) {
                throw new MojoExecutionException("Unable to create javascript directory - " + warDirectory.toString());
            }
        }

        for (int i = 0; i < dependencies.size(); i++) {
            Artifact artifact = (Artifact) dependencies.get(i);
            String dependencyType = artifact.getType();
            getLog().info(artifact.getArtifactId());
            if (dependencyType.equals("javascript")) {
//                getLog().info("Extracting " + dependency.getGroupId() + "-" + dependency.getArtifactId());
//                Artifact artifact = (Artifact) project.getArtifactMap().get(dependency.getGroupId() + ":" + dependency.getArtifactId());
                try {
                    javaScriptArtifactManager.unpack(artifact, warDirectory, false);
                } catch (ArchiverException e) {
                    throw new MojoExecutionException("Failed to unpack javascript dependency " + artifact, e);
                }
            }

        }

		getLog().info("Finished WarMojo");
    }
}