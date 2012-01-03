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

package com.nimbits.client.model.category;

import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.point.Point;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/15/11
 * Time: 4:02 PM
 */
public interface Category extends Serializable {

    List<Point> getPoints();

    void setDiagrams(final List<Diagram> diagrams);

    void setPoints(final List<Point> p);

    void addPoint(final Point p);

    long getId();

    long getUserFK();

    void setUserFK(final long userFK);

    CategoryName getName();

    void setName(final CategoryName name);

    List<Diagram> getDiagrams();

    String getDescription();

    void setDescription(final String description);

    boolean isReadOnly();

    ProtectionLevel getProtectionLevel();

    void setProtectionLevel(final ProtectionLevel protectionLevel);

    String getUUID();

    void setUUID(final String uuid);

    String getHost();

    void setHost(final String host);

}
