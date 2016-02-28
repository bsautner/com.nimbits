/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.secure;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;



public class StringEncryption {
    private final int saltSize = 20;

    public String encrypt(String plainText, boolean forwardOnly) {

        String passwordSalt = RandomStringUtils.randomAscii(saltSize);

        String encoded;

        if (forwardOnly) {
            encoded = DigestUtils.sha512Hex(plainText + passwordSalt);
            return encoded;

        } else {
            //TODO this is why we can't have nice things. - improve
            encoded = new String(Base64.encodeBase64(plainText.getBytes()));
            String combinedEncoded = encoded + passwordSalt;
            return new String(Base64.encodeBase64(combinedEncoded.getBytes()));
        }

    }


    public String decrypt(String encryptedText) {

        byte[] decoded = Base64.decodeBase64(encryptedText.getBytes());
        String step1 = new String(decoded);

        String step2 = step1.substring(0, step1.length() - saltSize);

        return new String(Base64.decodeBase64(step2.getBytes()));

    }
}
