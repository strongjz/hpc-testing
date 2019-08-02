// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.amazonaws.hpc.training;

import java.util.StringTokenizer;

public class Position {
    private String name;
    private int qty;
    private double strikePx;
    private double currentPx;
    private double sigma;

    public Position(String name, int qty, double strike, double current, double sigma) {
        this.name = name;
        this.qty = qty;
        this.strikePx = strike;
        this.currentPx = current;
        this.sigma = sigma;
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public double getStrikePx() {
        return strikePx;
    }

    public double getCurrentPx() {
        return currentPx;
    }

    public double getSigma() {
        return sigma;
    }

    public String toString() {
        return String.format("%s %d %.2f %.2f %.2f", name, qty, strikePx, currentPx, sigma);
    }

    static Position fromStr(String str) {
        StringTokenizer itr = new StringTokenizer(str);
        String name = itr.nextToken();
        int qty = Integer.valueOf(itr.nextToken());
        double strikePx = Double.valueOf(itr.nextToken());
        double currentPx = Double.valueOf(itr.nextToken());
        double sigma = Double.valueOf(itr.nextToken());
        return new Position(name, qty, strikePx, currentPx, sigma);
    }
}
