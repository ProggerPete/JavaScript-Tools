package net.dynamic_tools.maven.plugin.javascript.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;


/**
 * Goal which packages scripts and resources as a javascript archive to be
 * installed / deployed in maven repositories.
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 * @goal package
 * @phase package
 */
public class PackageMojo extends AbstractJavaScriptMojo {
    /**
     * Create the distribution archive with everything in the scripts directory,
     * and attach a manifest (generate one if necessary).
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
		getLog().info("Executing PackageMojo");

		getLog().info("Creating javascript artifact");
		File javascriptSourceDirectory = new File(project.getBasedir(), javaScriptDirectory);
		createJavaScriptArchive(javascriptSourceDirectory);

		getLog().info("Finished PackageMojo");
    }
}
