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

package com.nimbits.server.task;

import com.google.gson.Gson;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.pointcategory.CategoryServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CategoryMaintTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CategoryMaintTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final Gson gson = GsonFactory.getInstance();
        resp.setContentType(Const.CONTENT_TYPE_HTML);

        final String j = req.getParameter(Const.PARAM_USER);
        final User u = gson.fromJson(j, UserModel.class);


        if (u != null) {
            List<Category> categories = CategoryServiceFactory.getInstance().getCategories(u, false, false, false);
            for (Category c : categories) {
                if (Utils.isEmptyString(c.getUUID())) {
                    log.info("Fixing category " + c.getName().getValue());
                    c.setUUID(UUID.randomUUID().toString());
                    c.setDescription("");
                    c.setProtectionLevel(ProtectionLevel.onlyMe);
                    CategoryServiceFactory.getInstance().updateCategory(u, c);

                }
            }

        }


    }


}

