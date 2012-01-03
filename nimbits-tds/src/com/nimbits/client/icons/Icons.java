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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.icons;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface Icons extends ClientBundle {

    static final Icons INSTANCE = GWT.create(Icons.class);

    @Source("PublishToWebHS.png")
    ImageResource publish();

    @Source("drop-add.gif")
    ImageResource addNew();

    @Source("diagram16.png")
    ImageResource diagram();

    @Source("point_low.png")
    ImageResource point_low();

    @Source("point_high.png")
    ImageResource point_high();

    @Source("point_idle.png")
    ImageResource point_idle();

    @Source("point_ok.png")
    ImageResource point_ok();

    @Source("category16.png")
    ImageResource category();

    @Source("chart24.png")
    ImageResource chart24();

    @Source("table.png")
    ImageResource table();

    @Source("RepeatHS.png")
    ImageResource refresh2();

    @Source("NewReportHS.png")
    ImageResource NewFolder();

    @Source("add16.gif")
    ImageResource add16();

    @Source("add24.gif")
    ImageResource add24();

    @Source("add32.gif")
    ImageResource add32();

    @Source("application_side_list.png")
    ImageResource side_list();

    @Source("application_form.png")
    ImageResource form();

    @Source("connect.png")
    ImageResource connect();

    @Source("user_add.png")
    ImageResource user_add();

    @Source("user_delete.png")
    ImageResource user_delete();

    @Source("accordion.gif")
    ImageResource accordion();

    @Source("add.gif")
    ImageResource add();

    @Source("delete.gif")
    ImageResource delete();

    @Source("calendar.gif")
    ImageResource calendar();

    @Source("menu-show.gif")
    ImageResource menu_show();

    @Source("list-items.gif")
    ImageResource list_items();

    @Source("album.gif")
    ImageResource album();

    @Source("text.png")
    ImageResource text();

    @Source("plugin.png")
    ImageResource plugin();

    @Source("music.png")
    ImageResource music();

    @Source("RefreshDocViewHS.png")
    ImageResource refresh();

    @Source("SaveAllHS.png")
    ImageResource SaveAll();

    @Source("PlayHS.png")
    ImageResource Play();

    @Source("PauseHS.png")
    ImageResource Pause();

    @Source("PrimaryKeyHS.png")
    ImageResource Key();

    @Source("Help.png")
    ImageResource Help();

    @Source("RestartHS.png")
    ImageResource Restart();

    @Source("TableHS.png")
    ImageResource Grid();

    @Source("PieChart3DHS.png")
    ImageResource PieChart();

    @Source("FormulaEvaluatorHS.png")
    ImageResource Formula();

    @Source("WarningHS.png")
    ImageResource Warning();

    @Source("graphhs.png")
    ImageResource graph();

    @Source("EditInformationHS.png")
    ImageResource edit();

    @Source("OrganizerHS.png")
    ImageResource Category();

    @Source("HomeHS.png")
    ImageResource Home();

    @Source("eps_closedHS.png")
    ImageResource email();

    @Source("Filter2HS.png")
    ImageResource average();

    @Source("excel.png")
    ImageResource excel();

    @Source("user_add.png")
    ImageResource addFriend();

    @Source("user_delete.png")
    ImageResource deleteFriend();
}
