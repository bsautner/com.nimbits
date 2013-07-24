package com.nimbits.cloudplatform.client.model.device;

import java.io.Serializable;

/**
 * Created by benjamin on 7/23/13.
 */
public interface Device extends Serializable{
    String getRegId();

    String getOwner();
}
