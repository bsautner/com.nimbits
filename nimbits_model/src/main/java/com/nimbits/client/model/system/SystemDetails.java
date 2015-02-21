package com.nimbits.client.model.system;

import java.io.Serializable;

public interface SystemDetails extends Serializable {

    boolean isGAE();

    String getVersion();
}
