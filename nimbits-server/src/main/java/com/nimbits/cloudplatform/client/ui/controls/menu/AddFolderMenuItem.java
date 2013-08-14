/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.ui.controls.menu;

import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.cloudplatform.client.ui.icons.Icons;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 10/9/12
 * Time: 9:39 AM
 */
public class AddFolderMenuItem extends MenuItem {


    private static final String D = "New Folder";

    public AddFolderMenuItem() {
        super(D);
        super.setToolTip(D);
        super.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.category()));

    }




}
