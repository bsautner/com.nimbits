package com.nimbits.cloudplatform.client.model.summary;

import com.nimbits.cloudplatform.client.enums.*;
import com.nimbits.cloudplatform.client.model.trigger.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:59 AM
 */
public interface Summary  extends Trigger, Serializable {

    SummaryType getSummaryType();

    long getSummaryIntervalMs();

    int getSummaryIntervalSeconds();

    Date getLastProcessed();

    void setLastProcessed(final Date date);


}
