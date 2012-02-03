package org.apache.maven.surefire.util;

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

/*
 * Copyright 2002-2005 the original author or authors.
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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <p>Copied from Spring framework to keep Java 1.3 compatability.</p>
 * <p/>
 * <p>Handy class for wrapping runtime Exceptions with a root cause.</p>
 * <p/>
 * <p>This time-honoured technique is no longer necessary in Java 1.4, which
 * finally provides built-in support for exception nesting. Thus exceptions in
 * applications written to use Java 1.4 need not extend this class. To ease
 * migration, this class mirrors Java 1.4's nested exceptions as closely as possible.
 * <p/>
 * <p>Abstract to force the programmer to extend the class. <code>getMessage</code>
 * will include nested exception information; <code>printStackTrace</code> etc will
 * delegate to the wrapped exception, if any.
 * <p/>
 * <p>The similarity between this class and the NestedCheckedException class is
 * unavoidable, as Java forces these two classes to have different superclasses
 * (ah, the inflexibility of concrete inheritance!).
 * <p/>
 * <p>As discussed in
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and
 * Development</a>, runtime exceptions are often a better alternative to checked exceptions.
 * However, all exceptions should preserve their stack trace, if caused by a
 * lower-level exception.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getMessage
 * @see #printStackTrace
 * @see NestedCheckedException
 */
public class NestedRuntimeException
    extends RuntimeException
{

    /**
     * Root cause of this nested exception
     */
    private final Throwable cause;

    /**
     * Construct a <code>NestedRuntimeException</code> with the specified detail message
     * and nested exception.
     *
     * @param msg the detail message
     * @param ex  the nested exception
     */
    public NestedRuntimeException( String msg, Throwable ex )
    {
        super( msg );
        this.cause = ex;
    }

    /**
     * Construct a <code>NestedRuntimeException</code> with the specified nested exception.
     *
     * @param ex the nested exception
     */
    public NestedRuntimeException( Throwable ex )
    {
        super();
        this.cause = ex;
    }

    /**
     * Return the nested cause, or <code>null</code> if none.
     * <p>Note that this will only check one level of nesting.
     * Use <code>getRootCause()</code> to retrieve the innermost cause.
     */
    public Throwable getCause()
    {
        // Even if you cannot set the cause of this exception other than through
        // the constructor, we check for the cause being "this" here, as the cause
        // could still be set to "this" via reflection: for example, by a remoting
        // deserializer like Hessian's.
        return ( this.cause == this ? null : this.cause );
    }

    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    public String getMessage()
    {
        if ( getCause() == null )
        {
            return super.getMessage();
        }
        else
        {
            return super.getMessage() + "; nested exception is " + getCause().getClass().getName() + ": "
                + getCause().getMessage();
        }
    }

    /**
     * Print the composite message and the embedded stack trace to the specified stream.
     *
     * @param ps the print stream
     */
    public void printStackTrace( PrintStream ps )
    {
        if ( getCause() == null )
        {
            super.printStackTrace( ps );
        }
        else
        {
            ps.println( this );
            getCause().printStackTrace( ps );
        }
    }

    /**
     * Print the composite message and the embedded stack trace to the specified writer.
     *
     * @param pw the print writer
     */
    public void printStackTrace( PrintWriter pw )
    {
        if ( getCause() == null )
        {
            super.printStackTrace( pw );
        }
        else
        {
            pw.println( this );
            getCause().printStackTrace( pw );
        }
    }
}