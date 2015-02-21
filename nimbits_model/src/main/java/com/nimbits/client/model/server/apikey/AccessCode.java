package com.nimbits.client.model.server.apikey;

import com.nimbits.client.model.common.SimpleValue;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Container for an Password KEY String
 */
public class AccessCode extends SimpleValue<String> implements Serializable {


    protected AccessCode(String aValue) {
        super(aValue);

    }

    protected AccessCode() {
        super("");

    }


    public static AccessCode getInstance(final String aValue) {
        return aValue == null ? createEmptyKey() : new AccessCode(aValue);
    }

    public static AccessCode createEmptyKey() {
        return new AccessCode();
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
