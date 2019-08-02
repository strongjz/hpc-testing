// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.amazonaws.hpc.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class VarCalculator {

    private static int DAYS_LEFT = 24;

    public static class OptionPriceMapper extends Mapper<Object, Text, Text, DoubleWritable> {

        private Text word = new Text("VaR");

        private long POSITION = 10000;

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            String new_px_str = itr.nextToken();
            String old_px_str = itr.nextToken();
            String strike_str = itr.nextToken();
            String rStr = itr.nextToken();
            String sigmaStr = itr.nextToken();

            double new_px = Float.parseFloat(new_px_str);
            double old_px = Float.parseFloat(old_px_str);
            double strike = Float.parseFloat(strike_str);
            double r = Float.parseFloat(rStr);
            double sigma = Float.parseFloat(sigmaStr);

            OptionPricer old_pricer = new OptionPricer(new OptionPricer.OptionParams(DAYS_LEFT + 1, old_px, strike, r, sigma));
            OptionPricer new_pricer = new OptionPricer(new OptionPricer.OptionParams(DAYS_LEFT, new_px, strike, r, sigma));

            double loss = (new_pricer.calc_option_px() - old_pricer.calc_option_px()) * POSITION;
			
			/*
			System.out.printf("strike=%.2f days=%d px=%.2f, r=%.2f, sigma=%.2f optionPx=%.2f\n", strike_px, days, px, r,
					sigma, optionPx);

			System.out.println(loss);
			*/

            context.write(word, new DoubleWritable(loss));
        }
    }

    public static class OptionPriceReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            List<Double> results = new ArrayList<>();
            for (DoubleWritable val : values) {
                results.add(val.get());
            }
            System.out.println("#Results=" + results.size());

            Collections.sort(results);
            double var = results.get((int) (results.size() * 0.05));
            context.write(key, new DoubleWritable(var));
            long elapsed_tm = System.currentTimeMillis() - start_tm;
            System.out.printf("Total time = %.2f secs\n", elapsed_tm / 1000.0);
        }
    }

    public static long start_tm = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "VaR Calculator");
        job.setJarByClass(VarCalculator.class);
        job.setMapperClass(OptionPriceMapper.class);
        job.setCombinerClass(OptionPriceReducer.class);
        job.setReducerClass(OptionPriceReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}