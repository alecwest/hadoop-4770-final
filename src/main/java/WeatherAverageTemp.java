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
import java.util.List;

public class WeatherAverageTemp {
    public static class WeatherAverageMapper
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

    public static class WeatherAverageReducer
            extends Reducer<Text, FloatWritable, Text, FloatWritable> {

        private FloatWritable result = new FloatWritable();

        public void reduce(Text key, Iterable<FloatWritable> values, Context context)
                throws IOException, InterruptedException {
            /**
             * This will iterate across a list of values associated with a key
             * and assign the max as the result
             */
            Float sum = (float) 0;
            int numValues = 0;
            for (FloatWritable value : values) {
                sum += value.get();
                numValues++;
            }
            result.set(sum / (float) numValues);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "weather average");
        job.setJarByClass(WeatherAverageTemp.class);
        job.setMapperClass(WeatherAverageTemp.WeatherAverageMapper.class);
        job.setCombinerClass(WeatherAverageTemp.WeatherAverageReducer.class);
        job.setReducerClass(WeatherAverageTemp.WeatherAverageReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
