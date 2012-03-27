/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.intelligence;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.intelligence.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.http.*;
import com.nimbits.server.point.*;
import com.nimbits.server.value.*;
import com.nimbits.server.settings.*;
import com.nimbits.server.user.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/18/11
 * Time: 1:44 PM
 */
public class IntelligenceServiceImpl extends RemoteServiceServlet implements IntelligenceService {
    private static final Logger log = Logger.getLogger(IntelligenceServiceImpl.class.getName());
    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }

    private String key() {
        try {
            return SettingsServiceFactory.getInstance().getSetting(SettingType.wolframKey);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
            return "";
        }

    }

    private String getTextFromResponse(final String responseXML) {
        DocumentBuilder db;
        String retVal = "";

        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseXML));
            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("plaintext");
            Node node = nodes.item(0);
            retVal = node.getTextContent();

        } catch (ParserConfigurationException e) {
            log.severe(e.getMessage());
        } catch (SAXException e) {
            log.severe(e.getMessage());
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (NullPointerException e) {
            log.info(e.getMessage());
        }

        return retVal;
    }

    public Map<String, String> getHTMLContent(final String responseXML) {
        Map<String, String> retObj = new HashMap<String, String>();
        DocumentBuilder db;
        try {


            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseXML));
            Document doc = db.parse(is);
            NodeList nodes = doc.getChildNodes();
            Node queryResult = nodes.item(0);
            NodeList x = queryResult.getChildNodes();

            for (int i = 0; i < x.getLength(); i++) {
                String name = x.item(i).getNodeName();
                if (name.equals("pod")) {
                    name += i;
                }
                retObj.put(name, x.item(i).getTextContent());


            }
            /// // / Node node = nodes.item(0);

        } catch (ParserConfigurationException e) {
            log.severe(e.getMessage());
        } catch (SAXException e) {
            log.severe(e.getMessage());
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return retObj;
    }


    public double getFormulaResult(final String formula) throws NimbitsException {

        // final String key = SettingTransactionsFactory.getInstance().getSetting(Const.PARAM_WOLFRAM_ALPHA_KEY);

        final String params;
        try {
            params = Params.PARAM_INPUT + "=" + URLEncoder.encode(formula, Const.CONST_ENCODING) +
                    "&appid=" + key() +
                    "&includepodid=Result";
        } catch (UnsupportedEncodingException e) {
            throw new NimbitsException(e.getMessage());
        }
        final String result = HttpCommonFactory.getInstance().doGet(Path.PATH_WOLFRAM_ALPHA, params);
        return Double.valueOf(result);


    }

    public String getDataResult(final String query, final String podId) throws NimbitsException {

        return getTextFromResponse(getRawResult(query, podId, false));

    }

    public String getRawResult(final String query, final String podId, final boolean htmlOutput) throws NimbitsException {
        String params;
        try {
            params = Params.PARAM_INPUT + "=" + URLEncoder.encode(query, Const.CONST_ENCODING) +
                    "&appid=" + key();
            if (!Utils.isEmptyString(podId)) {
                params += "&includepodid=" + podId;
            }
            if (htmlOutput) {
                params += "&format=html";
            }
        } catch (UnsupportedEncodingException e) {
            throw new NimbitsException(e.getMessage());
        }
        return HttpCommonFactory.getInstance().doGet(Path.PATH_WOLFRAM_ALPHA, params);
    }



    public String addDataToInput(final User u, final Intelligence i) throws NimbitsException {
        return addDataToInput(u, i.getInput());
    }

    @Override
    public Intelligence getIntelligence(Entity entity) {
        return IntelligenceServiceFactory.getDaoInstance().getIntelligence(entity);
    }

    @Override
    public Entity addUpdateIntelligence(final Entity entity, final EntityName name, final Intelligence update) throws NimbitsException {

        Entity retObj = null;
        User u = getUser();
        if (entity == null) {

            String uuid = UUID.randomUUID().toString();
            Entity e = EntityModelFactory.createEntity(name, "", EntityType.intelligence, ProtectionLevel.onlyMe, uuid,
                    update.getTrigger(), u.getUuid());
            retObj = EntityServiceFactory.getInstance().addUpdateEntity(u, e);
            Intelligence c = IntelligenceModelFactory.createIntelligenceModel(uuid,
                    update.getEnabled(), update.getResultTarget(), update.getTarget(), update.getInput(), update.getNodeId(),
                    update.getResultsInPlainText(), update.getTrigger());

            IntelligenceServiceFactory.getDaoInstance().addUpdateIntelligence(c);


        }
        else if (entity.getEntityType().equals(EntityType.point) && Utils.isEmptyString(update.getUUID())) {
            String uuid = UUID.randomUUID().toString();
            Entity e = EntityModelFactory.createEntity(name, "", EntityType.intelligence, ProtectionLevel.onlyMe, uuid,
                    entity.getEntity(), u.getUuid());
            retObj = EntityServiceFactory.getInstance().addUpdateEntity(e);
            Intelligence c = IntelligenceModelFactory.createIntelligenceModel(uuid,
                    update.getEnabled(), update.getResultTarget(), update.getTarget(), update.getInput(), update.getNodeId(),
                    update.getResultsInPlainText(), update.getTrigger());

            IntelligenceServiceFactory.getDaoInstance().addUpdateIntelligence(c);


        }
        else if (entity.getEntityType().equals(EntityType.intelligence)) {
            entity.setName(name);
            IntelligenceServiceFactory.getDaoInstance().addUpdateIntelligence(update);

            return EntityServiceFactory.getInstance().addUpdateEntity(entity);


        }

        return retObj;


    }

    @Override
    public void processIntelligence(final User u, final Point point) throws NimbitsException {
        List<Intelligence> list = IntelligenceServiceFactory.getDaoInstance().getIntelligence(point);

        for (Intelligence i : list) {
            try {
                Point target = PointServiceFactory.getInstance().getPointByUUID(i.getTarget());

                if (target!= null) {

                    Value v = processInput(i);
                    RecordedValueServiceFactory.getInstance().recordValue(u, target, v, true);

                }
            } catch (NimbitsException e) {
                i.setEnabled(false);
                FeedServiceFactory.getInstance().postToFeed(u, new NimbitsException("An error occured when processing an intelligence" +
                        " expression - intelligence on data point has been disabled  " + e.getMessage()));
                IntelligenceServiceFactory.getDaoInstance().addUpdateIntelligence(i);

            }

        }

    }

    @Override
    public Value processInput(final Intelligence update) throws NimbitsException {
        String processedInput = addDataToInput(getUser(), update.getInput());
        Point target = PointServiceFactory.getInstance().getPointByUUID(update.getTarget());
        return processInput(update, target, processedInput);

    }

    @Override
    public void deleteIntelligence(final User u, final Entity entity) {
        IntelligenceServiceFactory.getDaoInstance().deleteIntelligence(entity);

    }


    private String addDataToInput(final User u, final String input) throws NimbitsException {

        String retStr = input;

        if (input.contains(".data") || input.contains(".value") || input.contains(".note")) {

            String[] s = input.split("\\[");

            for (String k : s) {
                // System.out.println(k);
                if (k.contains(".data]") || k.contains(".value]") || k.contains(".note]")) {
                    String p = k.split("\\.")[0];
                    EntityName pointName = CommonFactoryLocator.getInstance().createName(p, EntityType.point);
                    String a = k.split("\\.")[1];

                    a = a.substring(0, a.indexOf("]"));
                    String r = "[" + pointName + "." + a + "]";
                    Point inputPoint;


                    Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
                    inputPoint= PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

                    if (inputPoint != null) {
                        Value inputValue = RecordedValueServiceFactory.getInstance().getCurrentValue(inputPoint);
                        if (a.equals(Params.PARAM_VALUE)) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getDoubleValue()));
                        } else if (a.equals(Params.PARAM_DATA)) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getData()));

                        } else if (a.equals(Params.PARAM_NOTE)) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getNote()));

                        }
                    }


                }


            }

        }
        log.info(retStr);
        return retStr;
    }


    @Override
    public Value processInput(final Intelligence intelligence, final Point targetPoint, final String processedInput) throws NimbitsException {

        final String result;


        String podId = intelligence.getNodeId();
        Double v = 0.0;
        String data = "";


        if (targetPoint == null) {
            throw new NimbitsException("Intelligence Processing Error, could not find target point");

        } else if (intelligence.getResultTarget() == IntelligenceResultTarget.value) {


            if (intelligence.getResultsInPlainText()) {
                result = getDataResult(processedInput, Params.PARAM_RESULT);
            } else {
                result = getRawResult(processedInput, Params.PARAM_RESULT, false);
            }


        } else {
            if (!Utils.isEmptyString(podId)) {
                if (intelligence.getResultsInPlainText()) {
                    result = getDataResult(processedInput, podId);
                } else {
                    result = getRawResult(processedInput, podId, false);
                }
            } else {
                result = getRawResult(processedInput, podId, false);
            }
        }
        if (intelligence.getResultTarget().equals(IntelligenceResultTarget.value)) {
            try {
                v = Double.valueOf(result);
            } catch (NumberFormatException e) {
                v =0.0;
                data = result;

            }
        } else {
            data = result;
        }

        return ValueModelFactory.createValueModel(0.0, 0.0, v, new Date(), targetPoint.getUUID(),"", data);


    }
}
