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

import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.user.User;
import com.nimbits.server.diagram.DiagramModelFactory;
import com.nimbits.server.diagram.DiagramServiceFactory;
import com.nimbits.server.user.UserModelFactory;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/2/11
 * Time: 11:04 AM
 */
public class TestDiagramServiceImpl {


    @Test
    public void testDiagramProtectionOwnerRequestsOwnedProtectedDiagram() {
        User u = UserModelFactory.createUserModel(1001);
        Diagram d = DiagramModelFactory.createDiagramModel(1001);
        User diagramOwner = UserModelFactory.createUserModel(1001);
        d.setProtectionLevel(0);
        //  u.addConnection(1002L);
        Assert.assertTrue(DiagramServiceFactory.getInstance().checkDiagramProtection(u, diagramOwner, d));
    }

    @Test
    public void testDiagramProtectionLoggedInUserRequestsOthersProtectedDiagram() {
        User loggedInUser = UserModelFactory.createUserModel(1001);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        d.setProtectionLevel(0);
        Assert.assertFalse(DiagramServiceFactory.getInstance().checkDiagramProtection(loggedInUser, diagramOwner, d));
    }

    @Test
    public void testDiagramProtectionOwnerRequestsConnectionsProtectedDiagram() {
        User u = UserModelFactory.createUserModel(1001);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        d.setProtectionLevel(0);
        diagramOwner.addConnection(1001L);
        Assert.assertFalse(DiagramServiceFactory.getInstance().checkDiagramProtection(u, diagramOwner, d));
    }

    @Test
    public void testDiagramProtectionOwnerRequestsConnectionsSharedDiagram() {
        User loggedInUser = UserModelFactory.createUserModel(1001L);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        d.setProtectionLevel(1);
        diagramOwner.addConnection(1001L);
        Assert.assertTrue("connection accessing shared diagram", DiagramServiceFactory.getInstance().checkDiagramProtection(loggedInUser, diagramOwner, d));
    }

    @Test
    public void testDiagramProtectionOwnerRequestsNotInConnectionsSharedDiagram() {
        User loggedInUser = UserModelFactory.createUserModel(1001L);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        d.setProtectionLevel(1);
        //  diagramOwner.addConnection(1001L);
        Assert.assertFalse("accessing shared diagram but not a connection", DiagramServiceFactory.getInstance().checkDiagramProtection(loggedInUser, diagramOwner, d));
    }

    @Test
    public void testDiagramProtectionOwnerRequestsConnectionsPublicDiagram() {
        User loggedInUser = UserModelFactory.createUserModel(1001L);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        d.setProtectionLevel(2);
        //  diagramOwner.addConnection(1001L);
        Assert.assertTrue("connection accessing public diagram", DiagramServiceFactory.getInstance().checkDiagramProtection(loggedInUser, diagramOwner, d));
    }


    @Test
    public void testDiagramProtectionLoggedInUserRequestsOthersSharedDiagram() {
        User loggedInUser = UserModelFactory.createUserModel(1001);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        d.setProtectionLevel(1);
        Assert.assertFalse(DiagramServiceFactory.getInstance().checkDiagramProtection(loggedInUser, diagramOwner, d));
    }

    @Test
    public void testDiagramProtectionLoggedInUserRequestsConnectionsProtectedDiagram() {
        User u = UserModelFactory.createUserModel(1001);
        Diagram d = DiagramModelFactory.createDiagramModel(1002);
        User diagramOwner = UserModelFactory.createUserModel(1002);
        d.setProtectionLevel(0);
        u.addConnection(1003L);
        u.addConnection(1002L);
        Assert.assertFalse(DiagramServiceFactory.getInstance().checkDiagramProtection(u, diagramOwner, d));

    }

}
