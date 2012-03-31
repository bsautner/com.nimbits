/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.storage;

import com.google.appengine.api.files.*;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Date;
/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 2:19 PM
 */
@SuppressWarnings("serial")
public class StorageServlet  extends HttpServlet {
    public static final String BUCKETNAME = "nimbits_value_store";
    public static final String FILENAME = "testfile1";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createFile(resp, FILENAME + new Date().getTime());
    }

    private void appendFileSample(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world from java");
        FileService fileService = FileServiceFactory.getFileService();


        try {
            GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
                    .setBucket(BUCKETNAME)
                    .setKey(FILENAME)
                    .setMimeType("text/html")
                    .setAcl("public_read")
                    .addUserMetadata("myfield1", "my field value");
            AppEngineFile writableFile =
                    fileService.createNewGSFile(optionsBuilder.build());

        } catch (IOException e) {
            resp.getWriter().println("error:" + e.getMessage());
        }

        // Open a channel to write to it
        boolean lock = false;
        String filename = "/gs/" + BUCKETNAME + "/" + FILENAME;
        AppEngineFile file = new AppEngineFile(filename);
        FileWriteChannel writeChannel =
                fileService.openWriteChannel(file, lock);
        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        out.println("The woods are lovely dark and deep.");
        out.println("But I have promises to keep." + new Date().getTime());
        // Close without finalizing and save the file path for writing later
        out.close();


        // At this point, the file is visible in App Engine as:
        // "/gs/BUCKETNAME/FILENAME"
        // and to anybody on the Internet through Cloud Storage as:
        // (http://commondatastorage.googleapis.com/BUCKETNAME/FILENAME)
        // We can now read the file through the API:

        FileReadChannel readChannel =
                fileService.openReadChannel(file, false);
        // Again, different standard Java ways of reading from the channel.
        BufferedReader reader =
                new BufferedReader(Channels.newReader(readChannel, "UTF8"));
        String line = reader.readLine();
        resp.getWriter().println("READ:" + line);

        // line = "The woods are lovely, dark, and deep."
        readChannel.close();
    }

    private void createFile(HttpServletResponse resp, String fileName) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("createFile");
        FileService fileService = FileServiceFactory.getFileService();


        GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
                .setBucket(BUCKETNAME)
                .setKey(fileName)
                .setMimeType("text/html")
                .setAcl("public_read")
                .addUserMetadata("myfield1", "my field value");
        AppEngineFile writableFile =
                fileService.createNewGSFile(optionsBuilder.build());
        // Open a channel to write to it
        boolean lock = false;
        FileWriteChannel writeChannel =
                fileService.openWriteChannel(writableFile, lock);
        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        out.println("The woods are lovely dark and deep.");
        out.println("But I have promises to keep.");
        // Close without finalizing and save the file path for writing later
        out.close();
        String path = writableFile.getFullPath();
        // Write more to the file in a separate request:
       // writableFile = new AppEngineFile(path);

        writeChannel.close();

        resp.getWriter().println("Done writing...");


    }


    private void createFileSample(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world from java");
        FileService fileService = FileServiceFactory.getFileService();


        GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
                .setBucket(BUCKETNAME)
                .setKey(FILENAME)
                .setMimeType("text/html")
                .setAcl("public_read")
                .addUserMetadata("myfield1", "my field value");
        AppEngineFile writableFile =
                fileService.createNewGSFile(optionsBuilder.build());
        // Open a channel to write to it
        boolean lock = false;
        FileWriteChannel writeChannel =
                fileService.openWriteChannel(writableFile, lock);
        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        out.println("The woods are lovely dark and deep.");
        out.println("But I have promises to keep.");
        // Close without finalizing and save the file path for writing later
        out.close();
        String path = writableFile.getFullPath();
        // Write more to the file in a separate request:
        writableFile = new AppEngineFile(path);
        // Lock the file because we intend to finalize it and
        // no one else should be able to edit it
        lock = true;
        writeChannel = fileService.openWriteChannel(writableFile, lock);
        // This time we write to the channel using standard Java
        writeChannel.write(ByteBuffer.wrap
                ("And miles to go before I sleep.".getBytes()));

        // Now finalize
        writeChannel.closeFinally();
        resp.getWriter().println("Done writing...");

        // At this point, the file is visible in App Engine as:
        // "/gs/BUCKETNAME/FILENAME"
        // and to anybody on the Internet through Cloud Storage as:
        // (http://commondatastorage.googleapis.com/BUCKETNAME/FILENAME)
        // We can now read the file through the API:
        String filename = "/gs/" + BUCKETNAME + "/" + FILENAME;
        AppEngineFile readableFile = new AppEngineFile(filename);
        FileReadChannel readChannel =
                fileService.openReadChannel(readableFile, false);
        // Again, different standard Java ways of reading from the channel.
        BufferedReader reader =
                new BufferedReader(Channels.newReader(readChannel, "UTF8"));
        String line = reader.readLine();
        resp.getWriter().println("READ:" + line);

        // line = "The woods are lovely, dark, and deep."
        readChannel.close();
    }
}
