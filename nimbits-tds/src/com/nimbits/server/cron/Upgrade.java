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

package com.nimbits.server.cron;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/13/12
 * Time: 12:58 PM
 */
public class Upgrade {


    private void doUpgrade() {


        //x make categories entities

        //x make points entities with cat as a parent

        //fix calcs to use uuids

        //x make alerts into multiple subscriptions - include different alerts (high and idle) as new subscriptios

        //x make connections from user into connection entities

        //x create entities for diagrams with type of file - make entity name end with .svg

        //x sync with core - all shared entities

        //recognise andriod client type - put links back in tree grid

        //x publish on hrd for users to test with

        //x test all buttons, export etc

        //review memcache

        //x create a summary point, options of 1, 8 and 24 hour - to create averaged avalues stored in a summary data point
        //x xmpp single points
        //x fix move task to only work with active points

        //next version


        //new context menus: read / write keys per point - make calcs / wolfram alpha intelligence entities


        //add wolfram alha results to search





    }

}
