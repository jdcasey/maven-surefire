package org.apache.maven.surefire.its;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.surefire.its.fixture.HelperAssertions;
import org.apache.maven.surefire.its.fixture.IntegrationTestSuiteResults;
import org.apache.maven.surefire.its.fixture.OutputValidator;
import org.apache.maven.surefire.its.fixture.ReportTestSuite;
import org.apache.maven.surefire.its.fixture.SurefireIntegrationTestCase;

/**
 * Test running two test cases; confirms reporting works correctly
 *
 * @author <a href="mailto:dfabulich@apache.org">Dan Fabulich</a>
 */
public class TwoTestCasesIT
    extends SurefireIntegrationTestCase
{
    public void testTwoTestCases()
        throws Exception
    {
        unpack( "junit-twoTestCases" ).executeTest().verifyErrorFreeLog().assertTestSuiteResults( 2, 0, 0, 0 );
    }

    /**
     * Runs two tests encapsulated in a suite
     */
    public void testTwoTestCaseSuite()
        throws Exception
    {
        final OutputValidator outputValidator = unpack( "junit-twoTestCaseSuite" ).executeTest();
        outputValidator.verifyErrorFreeLog().assertTestSuiteResults( 2, 0, 0, 0 );
        List<ReportTestSuite> reports = HelperAssertions.extractReports( new File[]{ outputValidator.getBaseDir() } );
        Set<String> classNames = extractClassNames( reports );
        assertContains( classNames, "junit.twoTestCaseSuite.BasicTest" );
        assertContains( classNames, "junit.twoTestCaseSuite.TestTwo" );
        assertEquals( "wrong number of classes", 2, classNames.size() );
        IntegrationTestSuiteResults results = HelperAssertions.parseReportList( reports );
        HelperAssertions.assertTestSuiteResults( 2, 0, 0, 0, results );
    }

    private void assertContains( Set<String> set, String expected )
    {
        if ( set.contains( expected ) )
        {
            return;
        }
        fail( "Set didn't contain " + expected );
    }

    private Set<String> extractClassNames( List<ReportTestSuite> reports )
    {
        HashSet<String> classNames = new HashSet<String>();
        for ( ReportTestSuite suite : reports )
        {
            classNames.add( suite.getFullClassName() );
        }
        return classNames;
    }

    public void testJunit4Suite()
        throws Exception
    {
        final OutputValidator outputValidator = unpack( "junit4-twoTestCaseSuite" ).executeTest();
        outputValidator.verifyErrorFreeLog().assertTestSuiteResults( 2, 0, 0, 0 );

        List<ReportTestSuite> reports =
            HelperAssertions.extractReports( ( new File[]{ outputValidator.getBaseDir() } ) );
        Set<String> classNames = extractClassNames( reports );
        assertContains( classNames, "twoTestCaseSuite.BasicTest" );
        assertContains( classNames, "twoTestCaseSuite.Junit4TestTwo" );
        assertEquals( "wrong number of classes", 2, classNames.size() );
        IntegrationTestSuiteResults results = HelperAssertions.parseReportList( reports );
        HelperAssertions.assertTestSuiteResults( 2, 0, 0, 0, results );
    }

    public void testTestNGSuite()
        throws Exception
    {
        final OutputValidator outputValidator = unpack( "testng-twoTestCaseSuite" ).executeTest();
        outputValidator.verifyErrorFreeLog().assertTestSuiteResults( 2, 0, 0, 0 );
        List<ReportTestSuite> reports = HelperAssertions.extractReports( new File[]{ outputValidator.getBaseDir() } );
        Set<String> classNames = extractClassNames( reports );
        assertContains( classNames, "testng.two.TestNGTestTwo" );
        assertContains( classNames, "testng.two.TestNGSuiteTest" );
        assertEquals( "wrong number of classes", 2, classNames.size() );
        IntegrationTestSuiteResults results = HelperAssertions.parseReportList( reports );
        HelperAssertions.assertTestSuiteResults( 2, 0, 0, 0, results );
    }

}
