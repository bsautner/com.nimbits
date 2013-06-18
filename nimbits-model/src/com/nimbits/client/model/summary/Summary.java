package com.nimbits.client.model.summary;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.trigger.*;

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
