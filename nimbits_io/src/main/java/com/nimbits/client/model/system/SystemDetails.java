package com.nimbits.client.model.system;

import java.io.Serializable;

public interface SystemDetails extends Serializable {

    Boolean isGAE();

    String getVersion();
}
