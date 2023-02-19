package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        processArgs(args);
      //  System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        ArrayList<Long> timeList = new ArrayList<>();
        Random random = new Random();
        for(int len = 10_00_000; len <= 10_00_000_00; len += 10_00_000) {
            int[] array = new int[len];

            long minTime = Integer.MAX_VALUE;
            int thread = 0;
            int cut = 0;
            int availableThreads = Runtime.getRuntime().availableProcessors();
            for (int k = 1; k <= availableThreads; k++) {
               //System.out.println(k);
                int threadCount = k;
                int count = 0;
                ForkJoinPool myPool = new ForkJoinPool(threadCount);
                for (int j = 10; j <= 100; j++) {
                    count++;
                    ParSort.par = 0;
                    ParSort.cutoff = array.length * j / 100;
                    long time;
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000);
                    long startTime = System.currentTimeMillis();
                    for (int t = 0; t < 1; t++) {
                        ParSort.sort(array, 0, array.length, myPool);
                    }
                    long endTime = System.currentTimeMillis();
                    time = (endTime - startTime);
                    if (time < minTime) {
                        thread = k;
                        cut = ParSort.cutoff;
                        minTime = time;
                    }
                    timeList.add(time);
                    System.out.print(array.length+","+k+","+ParSort.cutoff+","+((double)ParSort.cutoff/array.length)*100+","+ParSort.par+","+time+"##");
                }
                System.out.println("\n");
            }
           // System.out.println("Array Size: "+array.length+" Thread : "+thread+"  "+"CutOff : "+cut+"  Cutoff Persent : "+((double)cut/array.length)*100);
           // System.out.println(array.length+","+thread+","+cut+","+((double)cut/array.length)*100+","+ParSort.par);
        }


        try {
            FileOutputStream fis = new FileOutputStream("./src/result.csv");
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            int j = 0;
            for (long i : timeList) {
                j++;
                String content = j==100?(double) i / 1 + "\n":(double) i / 1+",";
                if(j==100) j=0;
               // j++;
                bw.write(content);
                bw.flush();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}
