/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.ui.icons;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;


public interface Icons extends ImageBundle {

    Icons INSTANCE = GWT.create(Icons.class);

    @Resource("connect.png")
    AbstractImagePrototype socket();

    @Resource("logout.png")
    AbstractImagePrototype logout();

    @Resource("delta.png")
    AbstractImagePrototype delta();

    @Resource("History.png")
    AbstractImagePrototype schedule();

    @Resource("download.png")
    AbstractImagePrototype download();

    @Resource("json.png")
    AbstractImagePrototype json();

    @Resource("email.png")
    AbstractImagePrototype email2();

    @Resource("line-chart.png")
    AbstractImagePrototype lineChart();

    @Resource("bug-blue.png")
    AbstractImagePrototype bug();

    @Resource("expand.png")
    AbstractImagePrototype summary();

    @Resource("web.png")
    AbstractImagePrototype web();

    @Resource("radial.png")
    AbstractImagePrototype radial();

    @Resource("link_web.png")
    AbstractImagePrototype connection();

    @Resource("expand.png")
    AbstractImagePrototype expand();

    @Resource("PublishToWebHS.png")
    AbstractImagePrototype webhook();

    @Resource("drop-add.gif")
    AbstractImagePrototype addNew();

    @Resource("diagram16.png")
    AbstractImagePrototype diagram();

    @Resource("point_low.png")
    AbstractImagePrototype point_low();

    @Resource("point_high.png")
    AbstractImagePrototype point_high();

    @Resource("point_idle.png")
    AbstractImagePrototype point_idle();

    @Resource("point_ok.png")
    AbstractImagePrototype point_ok();

    @Resource("category16.png")
    AbstractImagePrototype category();

    @Resource("chart24.png")
    AbstractImagePrototype chart24();

    @Resource("table.png")
    AbstractImagePrototype table();

    @Resource("RepeatHS.png")
    AbstractImagePrototype refresh2();

    @Resource("NewReportHS.png")
    AbstractImagePrototype NewFolder();

    @Resource("add16.gif")
    AbstractImagePrototype add16();

    @Resource("add24.gif")
    AbstractImagePrototype add24();

    @Resource("add32.gif")
    AbstractImagePrototype add32();

    @Resource("application_side_list.png")
    AbstractImagePrototype side_list();

    @Resource("application_form.png")
    AbstractImagePrototype form();

    @Resource("user_add.png")
    AbstractImagePrototype user_add();

    @Resource("user_delete.png")
    AbstractImagePrototype user_delete();

    @Resource("accordion.gif")
    AbstractImagePrototype accordion();

    @Resource("add.gif")
    AbstractImagePrototype add();

    @Resource("delete.gif")
    AbstractImagePrototype delete();

    @Resource("calendar.gif")
    AbstractImagePrototype calendar();

    @Resource("menu-show.gif")
    AbstractImagePrototype menu_show();

    @Resource("list-items.gif")
    AbstractImagePrototype list_items();

    @Resource("album.gif")
    AbstractImagePrototype album();

    @Resource("text.png")
    AbstractImagePrototype text();

    @Resource("plugin.png")
    AbstractImagePrototype plugin();

    @Resource("music.png")
    AbstractImagePrototype music();

    @Resource("RefreshDocViewHS.png")
    AbstractImagePrototype refresh();

    @Resource("SaveAllHS.png")
    AbstractImagePrototype SaveAll();

    @Resource("PlayHS.png")
    AbstractImagePrototype play();

    @Resource("PauseHS.png")
    AbstractImagePrototype Pause();

    @Resource("PrimaryKeyHS.png")
    AbstractImagePrototype key();

    @Resource("Help.png")
    AbstractImagePrototype Help();

    @Resource("RestartHS.png")
    AbstractImagePrototype Restart();

    @Resource("TableHS.png")
    AbstractImagePrototype Grid();

    @Resource("PieChart3DHS.png")
    AbstractImagePrototype PieChart();

    @Resource("FormulaEvaluatorHS.png")
    AbstractImagePrototype formula();

    @Resource("WarningHS.png")
    AbstractImagePrototype Warning();

    @Resource("graphhs.png")
    AbstractImagePrototype graph();

    @Resource("EditInformationHS.png")
    AbstractImagePrototype edit();

    @Resource("OrganizerHS.png")
    AbstractImagePrototype Category();

    @Resource("HomeHS.png")
    AbstractImagePrototype Home();

    @Resource("eps_closedHS.png")
    AbstractImagePrototype email();

    @Resource("Filter2HS.png")
    AbstractImagePrototype filter();

    @Resource("excel.png")
    AbstractImagePrototype excel();

    @Resource("user_add.png")
    AbstractImagePrototype addFriend();

    @Resource("user_delete.png")
    AbstractImagePrototype deleteFriend();
}
