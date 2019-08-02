// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.amazonaws.hpc.training;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

public class SeriesGenerator {

//	public static long ONE_THOUSAND = 1000L;	
//	public static long TEN_THOUSAND = ONE_THOUSAND * 10L; 
//	public static long HUN_THOUSAND = TEN_THOUSAND * 10L; 
//	public static long ONE_MILLION  = HUN_THOUSAND * 10L; 
//	public static long TEN_MILLION  = ONE_MILLION * 10L; 
//	public static long HUN_MILLION = TEN_MILLION * 10L; 	
//	public static long ONE_BILLION = HUN_MILLION * 10L;
//	
//	public static long SERIES_LEN = ONE_BILLION;

    public static Random rand = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException {

        long SERIES_LEN = Long.parseLong(args[0]);
        double old_px = Double.parseDouble(args[1]);
        double strike = Double.parseDouble(args[2]);
        double rate = Double.parseDouble(args[3]);
        double sig = Double.parseDouble(args[4]);
        FileWriter writer = new FileWriter(args[5]);

//		double curr_stock1_px = STOCK1_CURR_PX;
//		double curr_sigma = 2;
//		double curr_int_rate = 3;

        NormalDistribution stock1_dist = new NormalDistribution(0, 0.1);
        NormalDistribution sigma_dist = new NormalDistribution(0, 0.1);
        NormalDistribution intrate_dist = new NormalDistribution(0, 0.1);

        for (int i = 0; i < SERIES_LEN; ++i) {
            double new_px = old_px * (1 + stock1_dist.inverseCumulativeProbability(rand.nextDouble()));
            double sigma = sig * (1 + sigma_dist.inverseCumulativeProbability(rand.nextDouble()));
            double int_rate = rate * (1 + intrate_dist.inverseCumulativeProbability(rand.nextDouble()));

            writer.write(String.format("%.2f %.2f %.2f %.2f %.2f\n", new_px, old_px, strike, int_rate, sigma));
        }

        writer.close();
    }

}
