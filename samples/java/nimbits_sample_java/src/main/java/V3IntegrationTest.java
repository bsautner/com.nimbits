public class V3IntegrationTest {


    public static void main(String[] args) throws InterruptedException {



        Test loadTester = new Test();
        loadTester.execute();


    }
    private static class Test extends NimbitsTest {

        @Override
        public void execute() throws InterruptedException {
            super.execute();
            o("Startup...");
        }




    }

}
