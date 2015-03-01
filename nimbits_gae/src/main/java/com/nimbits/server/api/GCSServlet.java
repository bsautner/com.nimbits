package com.nimbits.server.api;

import com.google.appengine.tools.cloudstorage.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;


public class GCSServlet extends HttpServlet {
    public static final String BUCKETNAME = "nimbits02-bucket";
    public static final String FILENAME = "owner/pointname/ExampleFileName";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world from java");
        GcsService gcsService = GcsServiceFactory.createGcsService();
        GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);
        GcsFileOptions options = new GcsFileOptions.Builder()
                .mimeType("text/html")
                .acl("public-read")
                .addUserMetadata("myfield1", "my field value")
                .build();


        GcsOutputChannel writeChannel = gcsService.createOrReplace(filename, options);
        // You can write to the channel using the standard Java methods.
        // Here we use a PrintWriter:
        PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        writer.println("The woods are lovely dark and deep.");
        writer.println("But I have promises to keep.");
        writer.flush();

        // Note that the writeChannel is Serializable, so it is possible to store it somewhere and write
        // more to the file in a separate request. To make the object as small as possible call:
        writeChannel.waitForOutstandingWrites();

        // This time we write to the channel directly
        writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep.".getBytes("UTF8")));

        // If you want partial content saved in case of an exception, close the
        // GcsOutputChannel in a finally block. See the GcsOutputChannel interface
        // javadoc for more information.
        writeChannel.close();
        resp.getWriter().println("Done writing...");

        // At this point, the file is visible to anybody on the Internet through Cloud Storage as:
        // (http://storage.googleapis.com/BUCKETNAME/FILENAME)

        GcsInputChannel readChannel = null;
        BufferedReader reader = null;
        try {
            // We can now read the file through the API:
            readChannel = gcsService.openReadChannel(filename, 0);
            // Again, different standard Java ways of reading from the channel.
            reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            String line;
            // Prints "The woods are lovely, dark, and deep."
            // "But I have promises to keep."
            // "And miles to go before I sleep."
            while ((line = reader.readLine()) != null) {
                resp.getWriter().println("READ:" + line);
            }
        } finally {
            if (reader != null) { reader.close(); }
        }
    }
}
