package com.nimbits.client.model.relationship;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:47 AM
 */
public interface Relationship extends Serializable {

    String getKey();

    String getForeignKey();

}
