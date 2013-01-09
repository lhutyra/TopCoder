public class PolygonEstimationVis {
    private String[] args;

    public PolygonEstimationVis(String[] args) {
        this.args = args;
    }

    public void processTestCase() {
        try {
            VisParams.parseParams(args);

            TestCase.generate();

            SolutionRunner.runTestCase();
        } catch (Exception e) {
            SolutionRunner.stopSolution();
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new PolygonEstimationVis(args).processTestCase();
    }
}