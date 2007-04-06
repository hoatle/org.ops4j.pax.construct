package org.ops4j.pax.construct;

/*
 * Copyright 2007 Stuart McCulloch
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

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Create a new skeleton bundle and add it to an existing OSGi project.
 * 
 * @goal create-bundle
 */
public class OSGiBundleArchetypeMojo extends AbstractArchetypeMojo
{
    /**
     * The package of the new bundle.
     * 
     * @parameter expression="${package}"
     * @required
     */
    private String packageName;

    /**
     * The name of the new bundle.
     * 
     * @parameter expression="${name}"
     * @required
     */
    private String bundleName;

    /**
     * The version of the new bundle.
     * 
     * @parameter expression="${version}" default-value="0.1.0-SNAPSHOT"
     */
    private String version;

    /**
     * @parameter expression="${activator}" default-value="true"
     */
    private boolean activator;

    protected boolean checkEnvironment()
        throws MojoExecutionException
    {
        return project.getArtifactId().equals( "compile-bundle" );
    }

    protected void addAdditionalArguments( Commandline commandLine )
    {
        commandLine.createArgument().setValue( "-DarchetypeArtifactId=maven-archetype-osgi-bundle" );

        commandLine.createArgument().setValue(
            "-DgroupId=" + project.getGroupId().replaceFirst( "\\.build$", ".bundles" ) );

        commandLine.createArgument().setValue( "-DpackageName=" + packageName );
        commandLine.createArgument().setValue( "-DartifactId=" + bundleName );
        commandLine.createArgument().setValue( "-Dversion=" + version );

        commandLine.createArgument().setValue( "-Duser.dir=" + project.getBasedir() );
    }

    protected void postProcess()
        throws MojoExecutionException
    {
        if ( activator == false )
        {
            FileSet activatorFiles = new FileSet();
            activatorFiles.setDirectory( project.getBasedir() + File.separator + bundleName );
            activatorFiles.addInclude( "src/main/java/**/Activator.java" );
            activatorFiles.addInclude( "src/main/java/**/ServiceImpl.java" );
            activatorFiles.addInclude( "src/main/resources/META-INF/details.bnd" );

            try
            {
                new FileSetManager( getLog(), debug ).delete( activatorFiles );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "I/O error while deleting files", e );
            }
        }
    }
}