package com.nimbits.server.settings;

import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/4/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SettingTransactions {
    String getSetting(SettingType setting) throws NimbitsException;

    void updateSetting(SettingType name, String newValue);

    String reloadCache() throws NimbitsException;

    Map<SettingType, String> getSettings() throws NimbitsException;

    void addSetting(SettingType name, String value);
}
