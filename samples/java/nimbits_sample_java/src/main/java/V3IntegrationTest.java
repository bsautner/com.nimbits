public class V3IntegrationTest {


    public void main(String ... args) throws InterruptedException {


        Test t = new Test();
        t.execute();


    }

    private class Test extends NimbitsTest {

        @Override
        public void execute() throws InterruptedException {
            super.execute();
            o("Startup...");
        }




    }

}
