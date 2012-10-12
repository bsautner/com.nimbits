package client.model.common.impl;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.EntityName;
import org.junit.Test;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/3/12
 * Time: 12:57 PM
 */
public class CommonFactoryImplTest {


    @Test(expected = NimbitsException.class)
     public void TestName1() throws NimbitsException {
         EntityName name = CommonFactory.createName("D%@D", EntityType.point);


    }
    @Test(expected = NimbitsException.class)
    public void TestName2() throws NimbitsException {
        EntityName name = CommonFactory.createName("D\"D", EntityType.point);


    }
    @Test(expected = NimbitsException.class)
    public void TestName3() throws NimbitsException {
        EntityName name = CommonFactory.createName("D?@D", EntityType.point);


    }
    @Test(expected = NimbitsException.class)
    public void TestName4() throws NimbitsException {
        EntityName name = CommonFactory.createName("D+@D", EntityType.point);


    }
    @Test(expected = NimbitsException.class)
    public void TestName7() throws NimbitsException {
        EntityName name = CommonFactory.createName("userNamespaceclazo%inf.uach.cl-gtempaccount.com", EntityType.point);


    }
    @Test
    public void TestName5() throws NimbitsException {
        EntityName name = CommonFactory.createName("im ok", EntityType.point);



    }
    @Test
    public void TestName6() throws NimbitsException {

            EntityName name = CommonFactory.createName("test@exmaple.com", EntityType.point);


    }
}
