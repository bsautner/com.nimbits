package com.nimbits.server.intelligence;

import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.point.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/21/12
 * Time: 11:48 AM
 */
public interface IntelligenceTransactions {

    public Intelligence getIntelligence(Entity entity);

    Intelligence addUpdateIntelligence(Intelligence update);

    List<Intelligence> getIntelligence(Point point);
}
