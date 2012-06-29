package com.nimbits.server.zip;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/27/12
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Compression {
    byte[] decompress(byte[] input) throws UnsupportedEncodingException, IOException, DataFormatException;
}
