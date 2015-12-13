public class V3IntegrationTest {


    public static void main(String[] args) throws InterruptedException {


        V3Sample1 v3Sample1 = new V3Sample1();
        v3Sample1.execute();

        V3CreateAndTestWebHooks createAndTestWebHooks = new V3CreateAndTestWebHooks();
        createAndTestWebHooks.execute();

        V3ApiChildrenTest v3ApiChildrenTest = new V3ApiChildrenTest();
        v3ApiChildrenTest.execute();

        V3EntityTests v3EntityTests = new V3EntityTests();
        v3EntityTests.execute();
    }


}
