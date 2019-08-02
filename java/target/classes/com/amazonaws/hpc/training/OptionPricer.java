// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.amazonaws.hpc.training;

import java.io.Serializable;

import org.apache.commons.math3.distribution.NormalDistribution;

public class OptionPricer implements Serializable {
    private static final long serialVersionUID = 5294557359966059686L;

    public static class OptionParams implements Serializable {
        private static final long serialVersionUID = 5293739252666842133L;

        private static int TRADING_DAYS_IN_YEAR = 252;

        /**
         * Time as days until maturity
         */
        public int timeAsDays;

        /**
         * Time as years until maturity
         */
        public double timeAsYears;

        /**
         * Spot (current price) of the underlying
         */
        public double spot_px;

        /**
         * Strike (target price) of the option
         */
        public double strike_px;

        /**
         * Risk free interest rate
         */
        public double int_rate;

        /**
         * Implicit volatility of the underlying
         */
        public double sigma;

        public OptionParams(int tDays, double spot, double strike, double rate, double sigma) {
            this.timeAsDays = tDays;
            this.timeAsYears = timeAsDays / TRADING_DAYS_IN_YEAR;
            this.spot_px = spot;
            this.strike_px = strike;
            this.int_rate = rate;
            this.sigma = sigma;
        }

        public OptionParams(final OptionParams p) {
            this.timeAsDays = p.timeAsDays;
            this.timeAsYears = p.timeAsYears;
            this.spot_px = p.spot_px;
            this.strike_px = p.strike_px;
            this.int_rate = p.int_rate;
            this.sigma = p.sigma;
        }
    }

    private NormalDistribution normal_dist = new NormalDistribution(0, 1);
    private final OptionParams init_params;
    private OptionParams params;

    public OptionPricer(final OptionParams intialParameters) {
        this.init_params = intialParameters;
        this.params = new OptionParams(this.init_params);
    }

    /**
     * https://en.wikipedia.org/wiki/Black%E2%80%93Scholes_model#Black%E2%80%93Scholes_formula
     *
     * @param spot price of the underlying
     * @return d1
     */
    private double calc_d1(final double spot_px) {
        double den = params.sigma * Math.sqrt(params.timeAsYears);
        double num = Math.log(spot_px / params.strike_px) + (params.int_rate - 0.5 * params.sigma * params.sigma) * params.timeAsYears;
        return num / den;
    }

    /**
     * https://en.wikipedia.org/wiki/Black%E2%80%93Scholes_model#Black%E2%80%93Scholes_formula
     *
     * @param d1
     * @return d2
     */
    private double calc_d2(final double d1) {
        double result = d1 - params.sigma * Math.sqrt(params.timeAsYears);
        return result;
    }

    /**
     * https://en.wikipedia.org/wiki/Black%E2%80%93Scholes_model#Black%E2%80%93Scholes_formula
     *
     * @return
     * @throws MathException,       InterruptedException
     * @throws InterruptedException
     */
    public double calc_option_px() {
        final double spot_px = params.spot_px;
        final double d1 = calc_d1(spot_px);
        final double d2 = calc_d2(d1);
        final double d1_term = spot_px * normal_dist.cumulativeProbability(d1);
        final double d2_term = params.strike_px * Math.exp(-1 * params.int_rate * params.timeAsYears) * normal_dist.cumulativeProbability(d2);
        double px = d1_term - d2_term;
        return px;
    }

    public static void main(String[] args) {

        Double px = Double.parseDouble(args[0]);
        Double strike = Double.parseDouble(args[1]);
        Integer days = Integer.parseInt(args[2]);
        Double r = Double.parseDouble(args[3]);
        Double s = Double.parseDouble(args[4]);

        OptionPricer opx1 = new OptionPricer(new OptionPricer.OptionParams(days, px, strike, r, s));

        System.out.println(opx1.calc_option_px());
    }
}