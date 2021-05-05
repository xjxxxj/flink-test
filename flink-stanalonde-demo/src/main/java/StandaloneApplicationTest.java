import org.apache.flink.configuration.Configuration;
import org.apache.flink.container.entrypoint.StandaloneApplicationClusterEntryPoint;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class StandaloneApplicationTest {


    public static void main(String[] args) {

        String[] myArgs = {
                "--configDir", "E:\\coding\\flink-test\\flink-stanalonde-demo\\conf",
                "--job-classname", "spendreport.FraudDetectionJob"
        };

        //StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        StandaloneApplicationClusterEntryPoint.main(myArgs);
    }

}
