
package com.couchbase.example.servlet;

import com.couchbase.example.servlet.misc.Helper;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.example.servlet.conn.BucketFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shows how to perform an asynchronous get by using the Observable API of
 * the Couchbase client.
 * 
 * @author David Maier <david.maier at couchbase.com>
 */

@WebServlet(name = "AsyncGetServlet", urlPatterns = {"/get/*"}, asyncSupported = true)
public class AsyncGetServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AsyncGetServlet.class.getName());
    
    private static AsyncBucket bucket;
    
    
    @Override
    public void init() throws ServletException {

        bucket = BucketFactory.getAsyncBucket();
        
    }

    /**
     * Implements the GET method by taking the key parmeter into account.
     * 
     * Performs an async. get operation against Couchbase.
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
        //Get request parameters
        String key = req.getParameter("key");
        LOG.log(Level.INFO, "Getting key {0}", key);
        
        //Check if the key param is valid
        if (Helper.isValid(key))
        {
           //The async servlet context
           AsyncContext aCtx = req.startAsync();

           //Set the content type
           resp.setContentType("application/json");    
           
           //Get the output stream writer
           PrintWriter out = resp.getWriter();
           
           //Perform the async operation, on complete close the async servlet context
           bucket.get(key).subscribe(
           
               d -> { LOG.info("Got the document."); out.write(d.content().toString());},
               t -> { LOG.severe(t.getMessage());},
               () -> { LOG.info("Closing the response."); aCtx.complete();}
           ); 
        }
    
    }
    
    
}
