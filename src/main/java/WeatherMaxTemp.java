import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherMaxTemp {
    public static class WeatherMaxMapper
            extends Mapper<LongWritable, Text, Text, FloatWritable> {

        private Text word = new Text();

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            /**
             * Will need to parse the csv line and get relevant data
             * The header may need to be pre-processed out... or it could be handled
             * as a special case (probably preferred)
             *
             * Each mapper will only parse one value (TMAX) from each line
             */
            List<String> elements = CsvRowParser.parseCsvRow(value.toString());

            String date;
            FloatWritable temp;

            try {
                date = elements.get(1).split("-")[0];
            } catch (Exception e) {
                System.out.println("Could not set element 1 as date: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            try {
                temp = new FloatWritable(Float.parseFloat(elements.get(6)));
            } catch (Exception e) {
                System.out.println("Could not set element 6 as temp: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            word.set(date);
            context.write(word, temp);
        }
    }

    public static class WeatherMaxReducer
            extends Reducer<Text, FloatWritable, Text, FloatWritable> {

        private FloatWritable result = new FloatWritable();

        public void reduce(Text key, Iterable<FloatWritable> values, Context context)
                throws IOException, InterruptedException {
            /**
             * This will iterate across a list of values associated with a key
             * and assign the max as the result
             */
            Float max = Float.MIN_VALUE;
            for (FloatWritable value : values) {
                max = Float.max(max, value.get());
            }
            result.set(max);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "weather max");
        job.setJarByClass(WeatherMaxTemp.class);
        job.setMapperClass(WeatherMaxTemp.WeatherMaxMapper.class);
        job.setCombinerClass(WeatherMaxTemp.WeatherMaxReducer.class);
        job.setReducerClass(WeatherMaxTemp.WeatherMaxReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
