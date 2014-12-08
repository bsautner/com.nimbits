package com.nimbits.client.model.server.apikey;

import com.nimbits.client.model.common.SimpleValue;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Container for an API KEY String
 *
 */
public class ApiKey extends SimpleValue<String> implements Serializable {

    protected ApiKey(String aValue) {
        super(aValue);

    }

    protected ApiKey() {
        super("");

    }


    public static ApiKey getInstance(final String aValue) {
        return new ApiKey(aValue);
    }

    public boolean matchesString(final String aValue) {

        if (isEmpty()) {
            return false;
        }
        if (StringUtils.isEmpty(aValue)) {
            return false;

        }

        return aValue.equals(getValue());

    }



}
