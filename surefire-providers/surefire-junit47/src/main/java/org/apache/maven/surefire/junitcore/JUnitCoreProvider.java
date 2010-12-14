package org.apache.maven.surefire.junitcore;

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

import org.apache.maven.surefire.NonAbstractClassFilter;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.apache.maven.surefire.providerapi.SurefireProvider;
import org.apache.maven.surefire.report.ReporterConfiguration;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.suite.RunResult;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.util.DirectoryScanner;
import org.apache.maven.surefire.util.ReflectionUtils;
import org.apache.maven.surefire.util.TestsToRun;
import org.junit.runner.notification.RunListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kristian Rosenvold
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class JUnitCoreProvider
    implements SurefireProvider
{
    private final ReporterFactory reporterFactory;

    private final ClassLoader testClassLoader;

    private final DirectoryScanner directoryScanner;

    private final JUnitCoreParameters jUnitCoreParameters;

    private final NonAbstractClassFilter scannerFilter;

    private final List<RunListener> customRunListeners;

    private TestsToRun testsToRun;

    private final ReporterConfiguration reporterConfiguration;

    @SuppressWarnings( { "UnusedDeclaration" } )
    public JUnitCoreProvider( ProviderParameters booterParameters )
    {
        this.reporterFactory = booterParameters.getReporterFactory();
        reporterConfiguration = booterParameters.getReporterConfiguration();
        this.testClassLoader = booterParameters.getTestClassLoader();
        this.directoryScanner = booterParameters.getDirectoryScanner();
        this.jUnitCoreParameters = new JUnitCoreParameters( booterParameters.getProviderProperties() );
        this.scannerFilter = new NonAbstractClassFilter();
        customRunListeners =
            createCustomListeners( booterParameters.getProviderProperties().getProperty( "listener" ) );

    }

    public Boolean isRunnable()
    {
        return Boolean.TRUE;
    }

    public Iterator getSuites()
    {
        testsToRun = scanClassPath();
        return testsToRun.iterator();
    }

    public RunResult invoke( Object forkTestSet )
        throws TestSetFailedException, ReporterException
    {
        final String message = "Concurrency config is " + jUnitCoreParameters.toString();
        reporterFactory.createReporter().writeMessage( message );

        if ( testsToRun == null )
        {
            testsToRun = forkTestSet == null ? scanClassPath() : TestsToRun.fromClass( (Class) forkTestSet );
        }
        ConcurrentReportingRunListener listener =
            ConcurrentReportingRunListener.createInstance( this.reporterFactory, this.reporterConfiguration,
                                                           jUnitCoreParameters.isParallelClasses(),
                                                           jUnitCoreParameters.isParallelBoth() );
        customRunListeners.add( 0, listener );

        JUnitCoreWrapper.execute( testsToRun, jUnitCoreParameters, customRunListeners );
        return reporterFactory.close();
    }

    private TestsToRun scanClassPath()
    {
        return directoryScanner.locateTestClasses( testClassLoader, scannerFilter );
    }

    // Todo; We really need a shared java-5 language level module for the providers.
    private List<RunListener> createCustomListeners( String listenerProperty )
    {
        List<RunListener> result = new LinkedList<RunListener>();
        if ( listenerProperty == null )
        {
            return result;
        }

        for ( String thisListenerName : listenerProperty.split( "," ) )
        {
            RunListener customRunListener =
                (RunListener) ReflectionUtils.instantiate( Thread.currentThread().getContextClassLoader(),
                                                           thisListenerName );
            result.add( customRunListener );
        }

        return result;
    }

}