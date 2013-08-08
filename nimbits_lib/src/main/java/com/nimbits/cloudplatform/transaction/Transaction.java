package com.nimbits.cloudplatform.transaction;


import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.android.AndroidControl;
import com.nimbits.cloudplatform.client.android.AndroidControlImpl;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.http.FlushType;
import com.nimbits.cloudplatform.http.HttpHelper;
import com.nimbits.cloudplatform.http.UrlContainer;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import org.apache.commons.lang3.Range;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Transaction {

    private static final UrlContainer VALUE_SERVICE = UrlContainer.getInstance("/service/v2/value");
    private static final UrlContainer SESSION_SERVICE = UrlContainer.getInstance("/service/v2/session");
    private static final UrlContainer SERIES_SERVICE = UrlContainer.getInstance("/service/v2/series");
    private static final UrlContainer TREE_SERVICE = UrlContainer.getInstance("/service/v2/tree");
    private static final UrlContainer ENTITY_SERVICE = UrlContainer.getInstance("/service/v2/entity");
    private static final Calendar dailyExpire = Calendar.getInstance();
    public final static Type pointListType = new TypeToken<List<PointModel>>() {
    }.getType();
    public final static Type valueListType = new TypeToken<List<ValueModel>>() {
    }.getType();
    public final static Type entityListType = new TypeToken<List<EntityModel>>() {
    }.getType();


    static {
        dailyExpire.add(Calendar.HOUR, 24);
        HttpHelper.init(Nimbits.cookie, GsonFactory.getInstance());
    }

    public static List<User> getSession() {
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.HOUR, 1);
        UrlContainer path = UrlContainer.combine(Nimbits.base, SESSION_SERVICE);

        return HttpHelper.doGet(UserModel.class,
                path,
                new ArrayList<BasicNameValuePair>(0),
                UserModel.class, true, false, false, expire.getTime());


    }

    public static List<User> getSession(String email, String key) {
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.HOUR, 1);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        UrlContainer path = UrlContainer.combine(Nimbits.base, SESSION_SERVICE);
        params.add((new BasicNameValuePair(Parameters.email.getText(), email)));
        params.add((new BasicNameValuePair(Parameters.key.getText(), key)));
        return HttpHelper.doGet(UserModel.class,
                path,
                params,
                UserModel.class, true, false, false, expire.getTime());


    }

    public static List<Value> getValue(final Entity entity) {
        UrlContainer path = UrlContainer.combine(Nimbits.base, VALUE_SERVICE);

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        return HttpHelper.doGet(ValueModel.class, path, params, valueListType, false);

    }


    public static <T> List<T> getTree() {

        UrlContainer path = UrlContainer.combine(Nimbits.base, TREE_SERVICE);
        return HttpHelper.doGet(EntityModel.class, path, new ArrayList<BasicNameValuePair>(0),
                entityListType, true, true, true, dailyExpire.getTime());


    }

    public static List<Value> postValue(final Entity entity, final Value value) {
        UrlContainer path = UrlContainer.combine(Nimbits.base, VALUE_SERVICE);

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
        String content = GsonFactory.getInstance().toJson(value);
        addAuthenticationParameters(params);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.json.getText(), content)));
        return HttpHelper.doPost(ValueModel.class, path, params, null, FlushType.none, false);


    }

    public static List<Value> getSeries(final Entity entity, final Range<Integer> range) {
        UrlContainer path = UrlContainer.combine(Nimbits.base, SERIES_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        addAuthenticationParameters(params);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.count.getText(), String.valueOf(range.getMaximum()))));

        List<Value> sample = HttpHelper.doGet(ValueModel.class, path, params, valueListType, true);
        return sample;

    }

    public static void deleteEntity(final Entity entity) {

        UrlContainer path = UrlContainer.combine(Nimbits.base, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), entity.getEntityType().toString())));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.delete.getCode())));
        HttpHelper.doPost(EntityModel.class, path, params, entityListType, FlushType.complete, false);

    }

    public static <T, K> List<T> addEntity(Entity entity, final Class<K> clz) {
        UrlContainer path = UrlContainer.combine(Nimbits.base, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        String json = GsonFactory.getInstance().toJson(entity);
        params.add((new BasicNameValuePair(Parameters.json.getText(), json)));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.create.getCode())));
        return HttpHelper.doPost(clz, path, params, entityListType, FlushType.complete, false);

    }


    public static <T> List<T> updateEntity(Entity entity, Class<T> clz) {
        UrlContainer path = UrlContainer.combine(Nimbits.base, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        String json = GsonFactory.getInstance().toJson(entity);
        params.add((new BasicNameValuePair(Parameters.json.getText(), json)));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.update.getCode())));
        return HttpHelper.doPost(clz, path, params, entityListType, FlushType.complete, true);


    }

    public static <T, K> List<T> getEntity(final SimpleValue<String> entityId, final EntityType type, final Class<K> clz) {
        UrlContainer path = UrlContainer.combine(Nimbits.base, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
        addAuthenticationParameters(params);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entityId.getValue())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), type.toString())));
        return HttpHelper.doGet(clz, path, params, EntityModel.class, true, true, false, dailyExpire.getTime());


    }

    private static void addAuthenticationParameters(List<BasicNameValuePair> params) {
        if (Nimbits.authKey != null && !Nimbits.authKey.isEmpty()) {
            params.add((new BasicNameValuePair(Parameters.key.getText(), Nimbits.authKey.get(0))));
        }
        params.add((new BasicNameValuePair(Parameters.email.getText(), Nimbits.session.getEmail().getValue())));
    }


    public static List<AndroidControl> getControl() {
        HttpClient client = new DefaultHttpClient();
        String getURL = "http://nimbits-gcm.appspot.com/android";
        HttpGet get = new HttpGet(getURL);
        HttpResponse responseGet = null;
        List<AndroidControl> result = new ArrayList<AndroidControl>(1);

        try {
            responseGet = client.execute(get);

        HttpEntity resEntityGet = responseGet.getEntity();
        if (resEntityGet != null) {
            // do something with the response
            String response = EntityUtils.toString(resEntityGet);
            if (!Utils.isEmptyString(response)) {
                AndroidControl c = GsonFactory.getInstance().fromJson(response, AndroidControlImpl.class);
                if (c != null) {
                    result.add(c);
                }
            }


        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;


    }
}
