package org.apache.maven.plugin.surefire.booterclient;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.maven.surefire.booter.BooterDeserializer;
import org.apache.maven.surefire.booter.ClassLoaderConfiguration;
import org.apache.maven.surefire.booter.ClasspathConfiguration;
import org.apache.maven.surefire.booter.ProviderConfiguration;
import org.apache.maven.surefire.booter.StartupConfiguration;
import org.apache.maven.surefire.booter.SystemPropertyManager;
import org.apache.maven.surefire.booter.TypeEncodedValue;
import org.apache.maven.surefire.report.ReporterConfiguration;
import org.apache.maven.surefire.testset.DirectoryScannerParameters;
import org.apache.maven.surefire.testset.RunOrderParameters;
import org.apache.maven.surefire.testset.TestArtifactInfo;
import org.apache.maven.surefire.testset.TestRequest;
import org.apache.maven.surefire.util.RunOrder;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Performs roundtrip testing of serialization/deserialization of the ProviderConfiguration
 *
 * @author Kristian Rosenvold
 */
public class BooterDeserializerProviderConfigurationTest
    extends TestCase
{

    public static final TypeEncodedValue aTestTyped = new TypeEncodedValue( String.class.getName(), "aTest" );

    private final String aUserRequestedTest = "aUserRequestedTest";

    private static ClassLoaderConfiguration getForkConfiguration()
    {
        return new ClassLoaderConfiguration( true, false );
    }

    // ProviderConfiguration methods
    public void testDirectoryScannerParams()
        throws IOException
    {

        File aDir = new File( "." );
        List<String> includes = new ArrayList<String>();
        List<String> excludes = new ArrayList<String>();
        includes.add( "abc" );
        includes.add( "cde" );
        excludes.add( "xx1" );
        excludes.add( "xx2" );

        ClassLoaderConfiguration forkConfiguration = getForkConfiguration();
        final StartupConfiguration testStartupConfiguration = getTestStartupConfiguration( forkConfiguration );
        ProviderConfiguration providerConfiguration = getReloadedProviderConfiguration();
        ProviderConfiguration read = saveAndReload( providerConfiguration, testStartupConfiguration );

        Assert.assertEquals( aDir, read.getBaseDir() );
        Assert.assertEquals( includes.get( 0 ), read.getIncludes().get( 0 ) );
        Assert.assertEquals( includes.get( 1 ), read.getIncludes().get( 1 ) );
        Assert.assertEquals( excludes.get( 0 ), read.getExcludes().get( 0 ) );
        Assert.assertEquals( excludes.get( 1 ), read.getExcludes().get( 1 ) );

    }

    public void testReporterConfiguration()
        throws IOException
    {
        DirectoryScannerParameters directoryScannerParameters = getDirectoryScannerParameters();
        ClassLoaderConfiguration forkConfiguration = getForkConfiguration();

        ProviderConfiguration providerConfiguration = getTestProviderConfiguration( directoryScannerParameters );

        final StartupConfiguration testProviderConfiguration = getTestStartupConfiguration( forkConfiguration );
        ProviderConfiguration reloaded = saveAndReload( providerConfiguration, testProviderConfiguration );

        assertTrue( reloaded.getReporterConfiguration().isTrimStackTrace().booleanValue() );
        assertNotNull( reloaded.getReporterConfiguration().getReportsDirectory() );
    }

    public void testTestArtifact()
        throws IOException
    {
        ProviderConfiguration reloaded = getReloadedProviderConfiguration();

        Assert.assertEquals( "5.0", reloaded.getTestArtifact().getVersion() );
        Assert.assertEquals( "ABC", reloaded.getTestArtifact().getClassifier() );
    }

    public void testTestRequest()
        throws IOException
    {
        ProviderConfiguration reloaded = getReloadedProviderConfiguration();

        TestRequest testSuiteDefinition = reloaded.getTestSuiteDefinition();
        List<?> suiteXmlFiles = testSuiteDefinition.getSuiteXmlFiles();
        File[] expected = getSuiteXmlFiles();
        Assert.assertEquals( expected[0], suiteXmlFiles.get( 0 ) );
        Assert.assertEquals( expected[1], suiteXmlFiles.get( 1 ) );
        Assert.assertEquals( getTestSourceDirectory(), testSuiteDefinition.getTestSourceDirectory() );
        Assert.assertEquals( aUserRequestedTest, testSuiteDefinition.getRequestedTest() );
    }

    public void testTestForFork()
        throws IOException
    {
        final ProviderConfiguration reloaded = getReloadedProviderConfiguration();
        Assert.assertEquals( aTestTyped, reloaded.getTestForFork() );

    }

    public void testFailIfNoTests()
        throws IOException
    {
        ProviderConfiguration reloaded = getReloadedProviderConfiguration();
        assertTrue( reloaded.isFailIfNoTests().booleanValue() );

    }

    private ProviderConfiguration getReloadedProviderConfiguration()
        throws IOException
    {
        DirectoryScannerParameters directoryScannerParameters = getDirectoryScannerParameters();
        ClassLoaderConfiguration forkConfiguration = getForkConfiguration();
        ProviderConfiguration booterConfiguration = getTestProviderConfiguration( directoryScannerParameters );
        final StartupConfiguration testProviderConfiguration = getTestStartupConfiguration( forkConfiguration );
        return saveAndReload( booterConfiguration, testProviderConfiguration );
    }

    private DirectoryScannerParameters getDirectoryScannerParameters()
    {
        File aDir = new File( "." );
        List<String> includes = new ArrayList<String>();
        List<String> excludes = new ArrayList<String>();
        includes.add( "abc" );
        includes.add( "cde" );
        excludes.add( "xx1" );
        excludes.add( "xx2" );

        return new DirectoryScannerParameters( aDir, includes, excludes, Boolean.TRUE,
                                               RunOrder.asString( RunOrder.DEFAULT ) );
    }

    private ProviderConfiguration saveAndReload( ProviderConfiguration booterConfiguration,
                                                 StartupConfiguration testProviderConfiguration )
        throws IOException
    {
        final ForkConfiguration forkConfiguration = ForkConfigurationTest.getForkConfiguration();
        Properties props = new Properties();
        BooterSerializer booterSerializer = new BooterSerializer( forkConfiguration, props );
        String aTest = "aTest";
        booterSerializer.serialize( booterConfiguration, testProviderConfiguration, aTest, "never" );
        final File propsTest =
            SystemPropertyManager.writePropertiesFile( props, forkConfiguration.getTempDirectory(), "propsTest", true );
        BooterDeserializer booterDeserializer = new BooterDeserializer( new FileInputStream( propsTest ) );
        return booterDeserializer.deserialize();
    }

    private ProviderConfiguration getTestProviderConfiguration( DirectoryScannerParameters directoryScannerParameters )
    {

        File cwd = new File( "." );
        ReporterConfiguration reporterConfiguration = new ReporterConfiguration( cwd, Boolean.TRUE );
        String aUserRequestedTestMethod = "aUserRequestedTestMethod";
        TestRequest testSuiteDefinition =
            new TestRequest( getSuiteXmlFileStrings(), getTestSourceDirectory(), aUserRequestedTest,
                             aUserRequestedTestMethod );
        RunOrderParameters runOrderParameters = new RunOrderParameters( RunOrder.DEFAULT, null );
        return new ProviderConfiguration( directoryScannerParameters, runOrderParameters, true, reporterConfiguration,
                                          new TestArtifactInfo( "5.0", "ABC" ), testSuiteDefinition, new Properties(),
                                          aTestTyped );
    }

    private StartupConfiguration getTestStartupConfiguration( ClassLoaderConfiguration classLoaderConfiguration )
    {
        ClasspathConfiguration classpathConfiguration = new ClasspathConfiguration( true, true );

        return new StartupConfiguration( "com.provider", classpathConfiguration, classLoaderConfiguration, "never",
                                         false );
    }

    private File getTestSourceDirectory()
    {
        return new File( "TestSrc" );
    }

    private File[] getSuiteXmlFiles()
    {
        return new File[]{ new File( "A1" ), new File( "A2" ) };
    }

    private List<String> getSuiteXmlFileStrings()
    {
        return Arrays.asList( new String[]{ "A1", "A2" } );
    }
}
