package com.couchbase.example.servlet.misc;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Just some helper methods
 * 
 * @author David Maier <david.maier at couchbase.com>
 */
public class Helper {
    
    private static final Logger LOG = Logger.getLogger(Helper.class.getName());
    
    public static boolean isValid(String input)
    {
        if (input == null || "".equals(input))
        {
            LOG.log(Level.WARNING, "Validation of input {0} failed.", input);
            return false;
        }
        
        return true;
    }
    
}
