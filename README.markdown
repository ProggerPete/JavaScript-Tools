Javascript Tools
====================

JavaScript Tools provides dependency management for JavaScript projects via maven.

To develop a JavaScript module add the following declarationg to your pom.xml plugins section.

	<plugin>
		<groupId>net.dynamic-tools.javascript.maven</groupId>
		<artifactId>javascript-maven-plugin</artifactId>
		<version>1.0.1</version>
		<extensions>true</extensions>
	</plugin>

Then to include your javascript project in another project just add it as a dependency.

	<dependency>
		<groupId>your.group.id</groupId>
		<artifactId>your-artifact</artifactId>
		<version>your-version</version>
		<type>javascript</type>
	</dependency>`

All of the javascript dependencies will be copied into the target/javaScriptDependencies folder. Rather than trying
to make assumptions about where you want these files to end up the plugin currently leaves it to the user to configure
what to do with these files.

For a war project you would add a maven resources plugin entry to place the javascript dependencies in the desired directory
with something like:

	<plugin>
		<artifactId>maven-resources-plugin</artifactId>
		<version>2.5</version>
		<executions>
			<execution>
				<id>copy-dependency-javascript</id>
				<phase>process-test-resources</phase>
				<goals>
					<goal>copy-resources</goal>
				</goals>
				<configuration>
					<outputDirectory>${project.build.directory}/${project.build.finalName}/js</outputDirectory>
					<resources>
						<resource>
							<directory>${project.build.directory}/javaScriptDependencies/runtime</directory>
							<filtering>false</filtering>
						</resource>
					</resources>
				</configuration>
			</execution>
		</executions>
	</plugin>

For automated testing there is a modified version of the jsamine maven plugin at https://github.com/ProggerPete/jasmine-maven-plugin
which supports loading dependencies specified in the javascript files as opposed to specifying all the files in the preload
sources.

This plugin is open source, if you'd like it to do something it doesn't currently do then have a go at adding it yourself. If you
think it's something that everyone will want then let me know and if suitable it will be merged into the main source.