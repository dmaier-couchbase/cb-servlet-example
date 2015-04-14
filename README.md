# Couchbase Servlet Example
An example application which shows how to use Java Servlets with the Couchbase's Reactive API. 

# Preamble
This example is related to the question how to implement a REST service by using the benefits of executing async. operations on side of the service.

The 2.x Servlet API (and REST frameworks those are relying on it) do not fully allow it. Therefore specific Servlet Containers provided specific solutions. Comet is E.G. a solution for Apache Tomcat ("Comet support allows a servlet to process IO asynchronously, receiving events when data is available for reading on the connection (rather than always using a blocking read), and writing data back on connections asynchronously (most likely responding to some event raised from some other source)."). So in summary a 2.x servlet was designed to block. If you would try to pass the response stream to a Callback function then you would end up in an IllegalStateException because the Servlet container may have to decided to close the stream before your result arrives. The Servlet 3.x solves this by providing an unified API for all common Servlet Containers.


