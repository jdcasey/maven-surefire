package consoleOutput;

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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test1
{
    @Test
    public void testSystemOut()
    {

        char c = 'C';
        System.out.print( "Sout" );
        System.out.print( "Again" );
        System.out.print( "\n" );
        System.out.print( c );
        System.out.println( "SoutLine" );
        System.out.println( "A" );
        System.out.println( "" );
        System.out.println( "==END==" );
    }

    @Test
    public void testSystemErr()
    {
        char e = 'E';
        System.err.print( "Serr" );
        System.err.print( "\n" );
        System.err.print( e );
        System.err.println( "SerrLine" );
        System.err.println( "A" );
        System.err.println( "" );
        System.err.println( "==END==" );
    }

}
