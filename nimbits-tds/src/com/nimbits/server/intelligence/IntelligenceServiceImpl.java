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
import com.nimbits.server.admin.settings.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/18/11
 * Time: 1:44 PM
 */
public class IntelligenceServiceImpl extends RemoteServiceServlet implements IntelligenceService {
    private static final Logger log = Logger.getLogger(IntelligenceServiceImpl.class.getName());
    private static final int INT = 1014;
    private static final Pattern COMPILE = Pattern.compile("\\.");
    private static final Pattern PATTERN = Pattern.compile("\\.");
    private static final Pattern COMPILE1 = Pattern.compile("\\[");

    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }

    private static String key() {
        try {
            return SettingsServiceFactory.getInstance().getSetting(SettingType.wolframKey);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
            return "";
        }

    }

    private static String getTextFromResponse(final String responseXML) {
        String retVal = "";

        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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

    @Override
    public Map<String, String> getHTMLContent(final String responseXML) {
        Map<String, String> retObj = new HashMap<String, String>(INT);
        try {


            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseXML));
            Document doc = db.parse(is);
            NodeList nodes = doc.getChildNodes();
            Node queryResult = nodes.item(0);
            NodeList x = queryResult.getChildNodes();

            for (int i = 0; i < x.getLength(); i++) {
                StringBuilder name = new StringBuilder(x.item(i).getNodeName());
                if (name.toString().equals("pod")) {
                    name.append(i);
                }
                retObj.put(name.toString(), x.item(i).getTextContent());


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


    public static double getFormulaResult(final String formula) throws NimbitsException {

        // final String key = SettingTransactionsFactory.getInstance().getSetting(Const.PARAM_WOLFRAM_ALPHA_KEY);

        final String params;
        try {
            params = Parameters.input.getText() + '=' + URLEncoder.encode(formula, Const.CONST_ENCODING) +
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

    @Override
    public String getRawResult(final String query, final String podId, final boolean htmlOutput) throws NimbitsException {
        String params;
        try {
            params = Parameters.input.getText() + '=' + URLEncoder.encode(query, Const.CONST_ENCODING) +
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



    @Override
    public String addDataToInput(final User u, final Intelligence i) throws NimbitsException {
        return addDataToInput(u, i.getInput());
    }


    @Override
    public void processIntelligence(final User u, final Entity point) throws NimbitsException {
        final List<Entity> list = EntityServiceFactory.getInstance().getEntityByTrigger(u, point, EntityType.intelligence);

        for (final Entity entity :  list) {
            Intelligence i = (Intelligence) entity;
            try {
               // Point target = PointServiceFactory.getInstance().getPointByKey(i.getTarget());
                final Entity target = EntityServiceFactory.getInstance().getEntityByKey(i.getTarget(), EntityType.point).get(0);

                if (target!= null) {

                    final Value v = processInput(i);
                    RecordedValueServiceFactory.getInstance().recordValue(u, target, v);

                }
            } catch (NimbitsException e) {
                i.setEnabled(false);
                FeedServiceFactory.getInstance().postToFeed(u, new NimbitsException("An error occured when processing an intelligence" +
                        " expression - intelligence on data point has been disabled  " + e.getMessage()));
                EntityServiceFactory.getInstance().addUpdateEntity(i);


            }

        }

    }

    @Override
    public Value processInput(final Intelligence update) throws NimbitsException {
        String processedInput = addDataToInput(getUser(), update.getInput());
        final Point target = (Point) EntityServiceFactory.getInstance().getEntityByKey(update.getTarget(), EntityType.point).get(0);

      //  Point target = PointServiceFactory.getInstance().getPointByKey(update.getTarget());
        return processInput(update, target, processedInput);

    }




    private static String addDataToInput(final User u, final String input) throws NimbitsException {

        String retStr = input;

        if (input.contains(".data") || input.contains(".value") || input.contains(".note")) {

            String[] s = COMPILE1.split(input);

            for (String k : s) {
                // System.out.println(k);
                if (k.contains(".data]") || k.contains(".value]") || k.contains(".note]")) {
                    String p = PATTERN.split(k)[0];
                    EntityName pointName = CommonFactoryLocator.getInstance().createName(p, EntityType.point);
                    String a = COMPILE.split(k)[1];

                    a = a.substring(0, a.indexOf(']'));
                    String r = "[" + pointName + '.' + a + ']';



                   // Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName,EntityType.point);
                    Entity inputPoint =  EntityServiceFactory.getInstance().getEntityByName(u, pointName, EntityType.point).get(0);

                    // inputPoint= PointServiceFactory.getInstance().getPointByKey(e.getKey());
                   // inputPoint = (Point) EntityServiceFactory.getInstance().getEntityByKey(e.getKey(), PointEntity.class.getName());

                    if (inputPoint != null) {
                        Value inputValue = RecordedValueServiceFactory.getInstance().getCurrentValue(inputPoint);
                        if (a.equals(Parameters.value.getText())) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getDoubleValue()));
                        } else if (a.equals(Parameters.data.getText())) {
                            retStr = retStr.replace(r, String.valueOf(inputValue.getData()));

                        } else if (a.equals(Parameters.note.getText())) {
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


        String podId = intelligence.getNodeId();


        if (targetPoint == null) {
            throw new NimbitsException("Intelligence Processing Error, could not find target point");

        }
        final String result = Utils.isEmptyString(podId) ? getRawResult(processedInput, podId, false)
                : getRawResult(processedInput, Parameters.result.getText(), false);

//                 intelligence.getResultsInPlainText()
//                ? getDataResult(processedInput, Parameters.result.getText())
//                :
//                :
//                : intelligence.getResultsInPlainText()
//                ? getDataResult(processedInput, podId)
//                : getRawResult(processedInput, podId, false);
        String data = "";
        Double v = 0.0;

            try {
                v = Double.valueOf(result);
            } catch (NumberFormatException e) {
                v =0.0;
                data = result;

            }


        return ValueModelFactory.createValueModel(0.0, 0.0, v, new Date(), data);


    }
}
