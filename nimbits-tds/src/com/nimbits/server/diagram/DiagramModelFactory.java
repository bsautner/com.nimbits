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

package com.nimbits.server.diagram;

import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.diagram.DiagramModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 8:06 PM
 */
public class DiagramModelFactory {


    public static DiagramModel createDiagramModel(Diagram p) {

        return new DiagramModel(p);


    }

    public static DiagramModel createDiagramModel(long ownerFk) {

        return new DiagramModel(ownerFk);


    }

    public static List<Diagram> createDiagramModels(List<Diagram> Diagrams) {
        List<Diagram> retObj = new ArrayList<Diagram>();

        for (Diagram p : Diagrams) {
            retObj.add(createDiagramModel(p));
        }

        return retObj;


    }
}
