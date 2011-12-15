package org.apache.maven.surefire;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.surefire.util.ScannerFilter;

public class OneOffTestClassFilter
    implements ScannerFilter
{

    private static final char FS = System.getProperty( "file.separator" ).charAt( 0 );

    private Set names;

    public OneOffTestClassFilter( String[] classNames )
    {
        if ( classNames != null && classNames.length > 0 )
        {
            this.names = new HashSet();
            for ( int i = 0; i < classNames.length; i++ )
            {
                String name = classNames[i];
                names.add( name.replace( FS, '.' ) );
            }
        }
    }

    public boolean accept( Class testClass )
    {
        if ( names != null && !names.isEmpty() )
        {
            // if we have an enumeration of one-off tests to run, and the
            // current test is in that list, accept it.
            return names.contains( testClass.getName() );
        }

        // If the tests enumeration is empty, allow anything.
        return true;
    }

}
