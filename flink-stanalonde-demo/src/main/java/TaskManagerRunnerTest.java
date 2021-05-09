import org.apache.flink.runtime.taskexecutor.TaskManagerRunner;

public class TaskManagerRunnerTest {

    public static void main(String[] args) throws Exception {
        String[] myArgs = {
                "--configDir", "E:\\coding\\flink-test\\flink-stanalonde-demo\\conf",
                "--job-classname", "spendreport.FraudDetectionJob"
        };
        TaskManagerRunner.main(myArgs);
    }

}
