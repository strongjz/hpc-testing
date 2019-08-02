// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.amazonaws.hpc.training;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PositionServer {
    private static List<Position> positions = new LinkedList<Position>();

    public static void loadPositions(String fileStr) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileStr));

        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            positions.add(Position.fromStr(line));
            line = reader.readLine();
        }
        reader.close();
    }

    public static List<Position> getPositions() {
        return positions;
    }

    public static void main(String[] args) throws IOException {
        PositionServer.loadPositions("positions.txt");
        List<Position> positions = getPositions();
        for (Position pos : positions) {
            System.out.println(pos);
        }
    }
}
