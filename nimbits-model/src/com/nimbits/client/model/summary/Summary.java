package com.nimbits.client.model.summary;

import com.nimbits.client.enums.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:59 AM
 */
public interface Summary  extends Serializable {
    String getUuid();

    String getEntity();

    String getTargetPointUUID();

    SummaryType getSummaryType();

    long getSummaryIntervalMs();

    int getSummaryIntervalHours();

    Date getLastProcessed();



}
