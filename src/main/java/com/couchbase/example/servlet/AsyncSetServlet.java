
package com.couchbase.example.servlet;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
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
 * Shows how to perform an asynchronous set by using the Observable API of
 * the Couchbase client.
 * 
 * @author David Maier <david.maier at couchbase.com>
 */

@WebServlet(name = "AsyncSetServlet", urlPatterns = {"/set/*"}, asyncSupported = true)
public class AsyncSetServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AsyncSetServlet.class.getName());
    
    private static AsyncBucket bucket;
    
    
    @Override
    public void init() throws ServletException {

        bucket = BucketFactory.getAsyncBucket();
        
    }
    
    
    /**
     * Implements the POST method by taking the key and value parameter into account.
     * Performs an async. set against Couchbase.
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     
        //Get request parameters
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        
        if (Helper.isValid(key) && Helper.isValid(value))
        {
           //The async servlet context
           AsyncContext aCtx = req.startAsync();
           
           //Set the content type
           resp.setContentType("application/json");    
           
           //Get the output stream writer
           PrintWriter out = resp.getWriter();
           
           //Create a JSON document from the input
           JsonObject json = JsonObject.empty();
           json.put("val", value);
           JsonDocument doc = JsonDocument.create(key, json);
           
           //Perform the async operation, on complete close the async servlet context
           bucket.upsert(doc).subscribe(
           
               d -> { LOG.info("Set the document."); out.write(d.content().toString());},
               t -> { LOG.severe(t.getMessage());},
               () -> { LOG.info("Closing the response."); aCtx.complete();}
           ); 
           
        }
    }  
    
    /**
     * This only exposes the doPost method as a GET method for testing purposes
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        this.doPost(req, resp);
    }

    
}
