# Couchbase Servlet Example
An example application which shows how to use Java Servlets with the Couchbase's Reactive API. 

## Preamble
This example is related to the question how to implement a REST service by using the benefits of executing async. operations on side of the service.m

The 2.x Servlet API (and REST frameworks those are relying on it) do not fully allow it. Therefore specific Servlet Containers provided specific solutions. Comet is E.G. a solution for Apache Tomcat ("Comet support allows a servlet to process IO asynchronously, receiving events when data is available for reading on the connection (rather than always using a blocking read), and writing data back on connections asynchronously (most likely responding to some event raised from some other source)."). So in summary a 2.x servlet was designed to have a blocking behaviour. If you would try to pass the response stream to a Callback function then you would end up in an IllegalStateException because the Servlet container may have decided to close the stream before your result could be written to it. The solution comes with the 3.x Servlet-API. It solves this issue by providing an unified API for all common Servlet Containers.

## Why do we even benefit from the async. framework (RxJava) which is used by Couchbase if using sync. Servlets?

Couchbase uses RxJava and so Observables in order to handle the async. results. You can even benefit from it if you want to perform multiple operations the same time by then waiting for the result. Here an example for a Multi-Get which is blocking at the end but which uses the async. API in order to send as much as requests over to Couchbase before waiting for the result. So this would also work with the synchronous Servlet (2.x Servlet API).

```
public List<JsonDocument> bulkGet(final Collection<String> ids) {
    return Observable
        .from(ids)
        .flatMap(new Func1<String, Observable<JsonDocument>>() {
            @Override
            public Observable<JsonDocument> call(String id) {
                return bucket.async().get(id);
            }
        })
        .toList()
        .toBlocking()
        .single();
}
```

## How an async. Servlet basically works

The 3.x Servlet API allows you to access an AsyncContext. This context can be used to complete the request processing as soon as the async. operation returns.

```
AsyncContext aCtx = req.startAsync();

// Do something async. ...

// As soon as completed call:
aCtx.complete();
```
## A Couchbase related example

The following very basic and simple example just shows how to use an async. Servlet in order to perform an async. Get operation against Couchbase Server:

```
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
```
