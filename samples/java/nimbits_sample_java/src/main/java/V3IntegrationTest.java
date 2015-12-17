public class V3IntegrationTest {


    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();

        V3EntityTests v3EntityTests = new V3EntityTests();
        v3EntityTests.execute();


        V3Sample1 v3Sample1 = new V3Sample1();
        v3Sample1.execute();

        V3CreateAndTestWebHooks createAndTestWebHooks = new V3CreateAndTestWebHooks();
        createAndTestWebHooks.execute();

        V3ApiChildrenTest v3ApiChildrenTest = new V3ApiChildrenTest();
        v3ApiChildrenTest.execute();



        V3RestClientTester v3RestClientTester = new V3RestClientTester();
        v3RestClientTester.execute();

        System.out.println("Integration Tests Completed in : " + ((System.currentTimeMillis() - start) / 1000));
    }


}
