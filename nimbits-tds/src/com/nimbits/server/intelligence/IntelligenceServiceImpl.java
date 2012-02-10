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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.IntelligenceResultTarget;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.client.service.intelligence.IntelligenceService;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.server.settings.SettingsServiceFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.shared.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/18/11
 * Time: 1:44 PM
 */
public class IntelligenceServiceImpl extends RemoteServiceServlet implements IntelligenceService {
    private static final Logger log = Logger.getLogger(IntelligenceServiceImpl.class.getName());


    private String key() {
        try {
            return SettingsServiceFactory.getInstance().getSetting(Const.SETTING_WOLFRAM);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
            params = Const.PARAM_INPUT + "=" + URLEncoder.encode(formula, Const.CONST_ENCODING) +
                    "&appid=" + key() +
                    "&includepodid=Result";
        } catch (UnsupportedEncodingException e) {
            throw new NimbitsException(e.getMessage());
        }
        final String result = HttpCommonFactory.getInstance().doGet(Const.PATH_WOLFRAM_ALPHA, params);
        return Double.valueOf(result);


    }

    public String getDataResult(final String query, final String podId) throws NimbitsException {

        return getTextFromResponse(getRawResult(query, podId, false));

    }

    public String getRawResult(final String query, final String podId, final boolean htmlOutput) throws NimbitsException {
        String params;
        try {
            params = Const.PARAM_INPUT + "=" + URLEncoder.encode(query, Const.CONST_ENCODING) +
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
        return HttpCommonFactory.getInstance().doGet(Const.PATH_WOLFRAM_ALPHA, params);
    }

    @Override
    public String processInput(final Point point,
                               final String input,
                               final String podId,
                               final IntelligenceResultTarget intelligenceResultTarget,
                               final EntityName targetEntityName,
                               final boolean getPlainText) throws NimbitsException {


        final User loggedInUser = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        final String retVal;

        final String processedInput = addDataToInput(loggedInUser, input);

        final Point targetPoint = PointServiceFactory.getInstance().getPointByName(loggedInUser, targetEntityName);


        if (targetPoint == null) {
            retVal = "Could not find the target point:" + targetEntityName.getValue() + " . You'll want to create it first.";

        } else if (targetPoint.getId() == point.getId()) {
            retVal = "Infinite loop error. You can't set the target point as the source point.";
        } else if (intelligenceResultTarget == IntelligenceResultTarget.value) {


            if (getPlainText) {
                retVal = getDataResult(processedInput, Const.PARAM_RESULT);
            } else {
                retVal = getRawResult(processedInput, Const.PARAM_RESULT, false);
            }


        } else {
            if (!Utils.isEmptyString(podId)) {
                if (getPlainText) {
                    retVal = getDataResult(processedInput, podId);
                } else {
                    retVal = getRawResult(processedInput, podId, false);
                }
            } else {
                retVal = getRawResult(processedInput, podId, false);
            }
        }
        return retVal;


    }

    public String addDataToInput(final User u, final Point point) throws NimbitsException {
        return addDataToInput(u, point.getIntelligence().getInput());
    }


    private String addDataToInput(final User u, final String input) throws NimbitsException {

        String retStr = input;

        if (input.contains(".data") || input.contains(".value") || input.contains(".note")) {

            String[] s = input.split("\\[");

            for (String k : s) {
                // System.out.println(k);
                if (k.contains(".data]") || k.contains(".value]") || k.contains(".note]")) {
                    String p = k.split("\\.")[0];
                    EntityName pointName = CommonFactoryLocator.getInstance().createName(p);
                    String a = k.split("\\.")[1];

                    a = a.substring(0, a.indexOf("]"));
                    String r = "[" + pointName + "." + a + "]";
                    Point inputPoint = PointServiceFactory.getInstance().getPointByName(u, pointName);
                    if (inputPoint != null) {
                        Value inputValue = RecordedValueServiceFactory.getInstance().getCurrentValue(inputPoint);
                        if (a.equals(Const.PARAM_VALUE)) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getNumberValue()));
                        } else if (a.equals(Const.PARAM_DATA)) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getData()));

                        } else if (a.equals(Const.PARAM_NOTE)) {
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
    public Value processInput(final Point point, final Point targetPoint, final String processedInput) throws NimbitsException {

        final String result;


        String podId = point.getIntelligence().getNodeId();
        Double v = 0.0;
        String data = "";


        if (targetPoint == null) {
            throw new NimbitsException("Intelligence Processing Error, could not find target point with id " + point.getIntelligence().getTargetPointId());


        } else if (targetPoint.getId() == point.getId()) {
            throw new NimbitsException("Infinite loop error. You can't set the target point as the source point.");

        } else if (point.getIntelligence().getResultTarget() == IntelligenceResultTarget.value) {


            if (point.getIntelligence().getResultsInPlainText()) {
                result = getDataResult(processedInput, Const.PARAM_RESULT);
            } else {
                result = getRawResult(processedInput, Const.PARAM_RESULT, false);
            }


        } else {
            if (!Utils.isEmptyString(podId)) {
                if (point.getIntelligence().getResultsInPlainText()) {
                    result = getDataResult(processedInput, podId);
                } else {
                    result = getRawResult(processedInput, podId, false);
                }
            } else {
                result = getRawResult(processedInput, podId, false);
            }
        }
        if (point.getIntelligence().getResultTarget().equals(IntelligenceResultTarget.value)) {
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
