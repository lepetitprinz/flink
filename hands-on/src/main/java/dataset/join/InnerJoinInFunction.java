package dataset.join;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

public class InnerJoinInFunction {
    private static final String DIR = System.getProperty("user.dir");
    private static final String INPUT1 = DIR + "data/input/dataset/person";
    private static final String INPUT2= DIR + "data/input/dataset/location";
    private static final String OUTPUT = DIR + "data/output/dataset/inner_join.csv";
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final ParameterTool params = ParameterTool.fromArgs(args);

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        // Read people data and generate tuples out of each string read
        DataSet<Tuple2<Integer, String>> personSet = env.readTextFile(INPUT1)
            .map(new MapFunction<String, Tuple2<Integer, String>>() {
                public Tuple2<Integer, String> map(String value) throws Exception {
                    String[] words = value.split(",");
                    return new Tuple2<Integer, String>(Integer.parseInt(words[0]), words[1]);
                }
            });

        // Read location data and generate tuples out of each string read
        DataSet<Tuple2<Integer, String>> locationSet = env.readTextFile(INPUT2)
            .map(new MapFunction<String, Tuple2<Integer, String>>() {
                public Tuple2<Integer, String> map(String value) throws Exception {
                    String[] words = value.split(",");
                    return new Tuple2<Integer, String>(Integer.parseInt(words[0]), words[1]);
                }
            });

        // Join datasets on person_id
        DataSet<Tuple3<Integer, String, String>> joined = personSet.join(locationSet)
            .where(0)
            .equalTo(0)
            .with((JoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple3<Integer, String, String>>) (person, location)
                -> new Tuple3<>(person.f0, person.f1, location.f1));

        joined.writeAsCsv(OUTPUT, "\n", " ");

        env.execute("Example: Inner Join");
    }
}
