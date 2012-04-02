package integration;/*
 * Copyright (c) 2011. Tonic Solutions, LLC. All Rights Reservered. This Code is distributed under GPL V3 without any warrenty.
 */

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 3/14/11
 * Time: 7:28 PM
 */
public class DataPointIntegrationTest extends TestCase {




    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        System.out.println("tear down waiting for tasks to finish");
        Thread.sleep(1000);
        // ClientHelper.client().deleteCategory(cat);
        System.out.println("tear down complete");
    }

    public void testNoCompression() throws Exception {
        final Point p = PointModelFactory.createPointModel();
        final EntityName name = CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString(), EntityType.point);
        p.setExpire(1);
        p.setFilterValue(0);
        final Point point =  ClientHelper.client().addPoint(name, p);
        assertNotNull(point);

        final double x = testCompression(name);
        ClientHelper.client().deletePoint(name);

        Assert.assertEquals(345.0, x);

        //   gClient.DeletePoint(p.getValue());
    }


    public void testCompressionSeparatePostsNoDate() throws Exception {
        Point p = PointModelFactory.createPointModel();
        EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString(), EntityType.point));
        p.setFilterValue(0.1);
        ClientHelper.client().addPoint(name);

        double rx = 0.0;

        try {

            for (int i = 0; i < 40; i++) {
                StringBuilder b = new StringBuilder();
                rx += 0.1;

                b.append("&p1=").append(URLEncoder.encode(name.getValue(), Const.CONST_ENCODING)).append("&v1=").append(rx);
                // System.out.println( b.toString());
                ClientHelper.client().recordBatch(b.toString());
                System.out.println(rx);
                Thread.sleep(1000);
            }

            Thread.sleep(2000);
            List<Value> v = ClientHelper.client().getSeries(name.getValue(), 10);
            double retVal = 0.0;
            for (Value x : v) {
                retVal += x.getDoubleValue();
                System.out.println(x.getDoubleValue());
            }

            DecimalFormat twoDForm = new DecimalFormat("#.##");
            retVal = Double.valueOf(twoDForm.format(retVal));
            ClientHelper.client().deletePoint(name.getValue());

            assertEquals(30.0, retVal, 0.0);

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }
    }


    public void testCompressionSeperateAlternatingValuesPostsNoDate() throws Exception {

        Point p = PointModelFactory.createPointModel();

        EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString(), EntityType.point));
        p.setFilterValue(0.1);
        Point result = ClientHelper.client().addPoint(name);
        assertNotNull(result);

        double rx = 0.0;

        try {


            for (int i = 0; i < 40; i++) {
                StringBuilder b = new StringBuilder();
                if (rx == 0.0) {
                    rx = 1.0;
                } else {
                    rx = 0.0;
                }

                b.append("&p2=").append(URLEncoder.encode(name.getValue(), Const.CONST_ENCODING)).append("&v2=").append(rx);
                // System.out.println( b.toString());
                System.out.println(ClientHelper.client().recordBatch(b.toString()));
                Thread.sleep(100);

            }
            Thread.sleep(3000);
            List<Value> v = ClientHelper.client().getSeries(name, 40);
            double retVal = 0.0;
            for (Value x : v) {
                retVal += x.getDoubleValue();

            }

            DecimalFormat twoDForm = new DecimalFormat("#.##");
            retVal = Double.valueOf(twoDForm.format(retVal));
            ClientHelper.client().deletePoint(name);
            Assert.assertEquals(20.0, retVal);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testCompression() throws NimbitsException {
        Point p = PointModelFactory.createPointModel();

       EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString()));
        p.setFilterValue(2.0);
        Point result = ClientHelper.client().addPoint(name, p);
        assertNotNull(result);
        Point test = ClientHelper.client().getPoint(name);
        assertNotNull(test);
        double x = testCompression(name);

       // Assert.assertEquals(255.0, x);
       // ClientHelper.client().deletePoint(p.getName());
    }


    public void testChangeCompression() throws NimbitsException {
        Point p = PointModelFactory.createPointModel();

       EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString()));
        p.setFilterValue(0.0);
        ClientHelper.client().addPoint(name, p);


        Point px = ClientHelper.client().getPoint(name);

        Assert.assertNotNull(px);
        px.setFilterValue(2.0);
        ClientHelper.client().updatePoint(px);


        Point px2 = ClientHelper.client().getPoint(name);
        Assert.assertEquals(2.0, px2.getFilterValue());
        Assert.assertEquals(px.getKey(), px2.getKey());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
          fail();
        }
        //double x2 = testCompression(p);
        ClientHelper.client().deletePoint(name);
        // Assert.assertEquals(255.0, x2);


        // gClient.DeletePoint(p.getValue());
    }


    public void TestZeroCompressionWithBatch() throws NimbitsException {
        Point p = PointModelFactory.createPointModel();

       EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString()));
        p.setFilterValue(0.0);
        ClientHelper.client().addPoint(name, p);
        System.out.println("Starting batch compression integration test compression = " + p.getFilterValue());

        StringBuilder b = new StringBuilder();

        try {

            for (int i = 0; i < 40; i++) {
                b.append("&p").append(i).append("=").append(URLEncoder.encode(name.getValue(), Const.CONST_ENCODING)).append("&v").append(i).append("=").append(i).append("&t").append(i).append("=").append(new Date().getTime());

                Thread.sleep(100);

            }
            System.out.println(b.toString());
            System.out.println(ClientHelper.client().recordBatch(b.toString()));

            double retVal = 0.0;

            Thread.sleep(1000);
            List<Value> v = ClientHelper.client().getSeries(name, 10);
            for (Value x : v) {
                retVal += x.getDoubleValue();
                System.out.println(x.getDoubleValue() + "  " + x.getTimestamp());
            }
            Assert.assertEquals(345.0, retVal);
            ClientHelper.client().deletePoint(name);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //				String ba = "&email=" + email + "&secret="  + secret +
        //				"&p1=" + URLEncoder.encode("b1", Const.CONST_ENCODING)+ "&v1=" + r.nextDouble() +
        //				"&p2=" + URLEncoder.encode("b2", Const.CONST_ENCODING)+ "&v2=" + r.nextDouble() +
        //				"&p3=" + URLEncoder.encode("b3", Const.CONST_ENCODING)+ "&v3=" + r.nextDouble() +
        //				"&p4=" + URLEncoder.encode("b4", Const.CONST_ENCODING)+ "&v4=" + r.nextDouble() +
        //				"&p5=" + URLEncoder.encode("b5", Const.CONST_ENCODING)+ "&v5=" + r.nextDouble() +
        //				"&p6=" + URLEncoder.encode("b6", Const.CONST_ENCODING)+ "&v6=" + r.nextDouble() +
        //				"&p7=" + URLEncoder.encode("b7", Const.CONST_ENCODING)+ "&v7=" + r.nextDouble();
        //
        //
    }

    @Test
    public void TestCompressionWithBatch() throws NimbitsException {
        Point p = PointModelFactory.createPointModel();

       EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString()));
        p.setFilterValue(2.0);
        ClientHelper.client().addPoint(name, p);
        StringBuilder b = new StringBuilder();

        try {

            for (int i = 0; i < 40; i++) {
                b.append("&p").append(i).append("=").append(URLEncoder.encode(name.getValue(), Const.CONST_ENCODING)).append("&v").append(i).append("=").append(i).append("&t").append(i).append("=").append(new Date().getTime());

                Thread.sleep(1000);

            }
            //  System.out.println(b.toString());
            System.out.println(ClientHelper.client().recordBatch(b.toString()));
            double retVal = 0.0;

            Thread.sleep(1000);
            List<Value> v = ClientHelper.client().getSeries(name, 10);
            for (Value x : v) {
                retVal += x.getDoubleValue();
                System.out.println(x.getDoubleValue() + "  " + x.getTimestamp());

            }
            Assert.assertEquals(255.0, retVal);
            ClientHelper.client().deletePoint(name);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
           fail();
        }

    }

//    @Test
//    public void loadRandomDataIntoB1UsingKey() throws InterruptedException, IOException {
//       NimbitsUser nx = new NimbitsUser(email, key);
//
//       NimbitsClient c =  NimbitsClientFactory.getInstance(nx, hostURL);
//       Random r = new Random();
//
//
//        for (int i = 0; i < 10; i++)
//        {
//         Value v = new Value();
//            v.setValue(r.nextDouble()*10);
//            v.setTimestamp(new Date());
//            System.out.println(c.recordValue("b1",v));
//
//
//                Thread.sleep(1000);
//
//
//        }
//    }


    public void testCompressionWithBatchWithMissingPoints() throws NimbitsException {
        final Point p = PointModelFactory.createPointModel();

        final EntityName name = (CommonFactoryLocator.getInstance().createName("test" + UUID.randomUUID().toString(), EntityType.point));

        p.setFilterValue(2.0);
        ClientHelper.client().addPoint(name, p);
        final StringBuilder b = new StringBuilder(1024);

        try {

            for (int i = 0; i < 40; i++) {
                b.append("&p")
                .append(i)
                .append('=')
                .append(URLEncoder.encode(name.getValue(), Const.CONST_ENCODING))
                .append("&v")
                .append(i)
                .append('=')
                .append(i)
                .append("&t")
                .append(i)
                .append('=')
                .append(new Date().getTime());
                Thread.sleep(100);

            }
               b.append("&p41=32423fsdfsdf&v41=324fsdsd");   //add garbage to make sure the service doesn't choke.
            System.out.println(b.toString());
            System.out.println(ClientHelper.client().recordBatch(b.toString()));
            double retVal = 0.0;

            Thread.sleep(2000);
            final List<Value> v = ClientHelper.client().getSeries(name, 10);
            for (final Value x : v) {
                retVal += x.getDoubleValue();
                System.out.println(x.getDoubleValue() + "  " + x.getTimestamp());
            }
            Assert.assertEquals(255.0, retVal);
            ClientHelper.client().deletePoint(name);
        } catch (IOException e) {
         fail();
        } catch (InterruptedException e) {
            fail();
        }

    }


    private static double testCompression(final EntityName name) throws NimbitsException {


        double retVal = 0.0;
        Value vx;
        try {
            for (int i = 0; i < 40; i++) {
                Thread.sleep(10);
                vx= ClientHelper.client().recordValue(name, i, new Date());
                assertNotNull(vx);
            }
            // Thread.sleep(2000);
            final List<Value> v = ClientHelper.client().getSeries(name, 10);
            for (final Value x : v) {
                retVal += x.getDoubleValue();
            }
        } catch (InterruptedException e) {

        }
        System.out.println("End compression integration test " + retVal);

        return retVal;


    }
}
