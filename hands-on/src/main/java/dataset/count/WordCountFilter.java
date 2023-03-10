package dataset.count;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class WordCountFilter {
    private static final String DIR = System.getProperty("user.dir");
    private static final String INPUT = DIR + "/data/input/dataset/wc.txt";
    private static final String OUTPUT = DIR + "/data/output/dataset/wcTokenized.csv";

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);

        // Set up the environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setGlobalJobParameters(params);

        DataSet<String> text = env.readTextFile(INPUT);

        DataSet<String> filtered = text.filter(new MyFilter());

        DataSet<Tuple2<String, Integer>> tokenized = filtered.map(new Tokenizer());

        // save the result
        tokenized.writeAsCsv(OUTPUT, "\n", " ");

    }

    public static final class MyFilter implements FilterFunction<String>{
        public boolean filter(String value) {
            return value.startsWith("N");
        }
    }

    private static final class Tokenizer implements MapFunction<String, Tuple2<String, Integer>> {
        public Tuple2<String, Integer> map(String value) {
            return new Tuple2<>(value, 1);
        }
    }

}
