package Client;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


public class MyClient {

    private static String address = "http://18.224.40.129:8080";
    //  private static String address = "https://wlcv8chfv5.execute-api.us-east-1.amazonaws.com/Prod/myresource";
    private static String port = "8080";

    private static int maxThreads = 32;

    private static int numIter = 100;

    private static double[] percent = {0.1,0.5,1,0.25};

    private static int maxDayNum = 1;

    private static int userPopulation = 100000;

    private static int maxSteps = 5000;

    private static String[] phases = {"Warmup phase","Loading phase","Peak phase","Cooldown phase"};

    private static int[][] timeIntervals = {{0,2},{3,7},{8,18},{19,23}};

    private static final String prefix = "HW2/webapi/myresource/";



    public static void main(String[] args) throws IOException {

        final Client client = ClientBuilder.newClient();

        for( int i = 0; i < percent.length; i++) {
            int numThreads = (int) (maxThreads * percent[i]);
            List<Statistic> list = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(30);


            System.out.println(phases[i] + " running");

            final int var = i;
            final long start = System.currentTimeMillis();
            for (int j = 0; j < numThreads; j++) {

                for(int t = 0; t < numIter; t++){
                    final Statistic statistic = new Statistic();
                    list.add(statistic);
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {

                            for (int k = 0; k < 3; k++) {
                                int users = ThreadLocalRandom.current().nextInt(1, userPopulation);
                                int intervals = ThreadLocalRandom.current().nextInt(timeIntervals[var][0], timeIntervals[var][1]);
                                int stepCounts = ThreadLocalRandom.current().nextInt(1, maxSteps);
                                long start1 = System.currentTimeMillis();
                                String res = client.target(address).path(prefix + users + "/1/" + intervals + "/" + stepCounts).request(MediaType.TEXT_PLAIN)
                                        .post(Entity.entity("", MediaType.TEXT_PLAIN), String.class);
                                long end1 = System.currentTimeMillis();
//                                System.out.println(res);
                                statistic.addLatency(end1-start1);
                                statistic.addPostRes(res);

                                long start2 = System.currentTimeMillis();
                                String res2 = client.target(address).path(prefix + "single/"+users + "/1").request(MediaType.TEXT_PLAIN)
                                        .get(String.class);
//                                System.out.println("???");
//                                System.out.println(res2);
                                long end2 = System.currentTimeMillis();
                                statistic.addLatency(end2-start2);
                                statistic.addGetRes(res2);

                            }


                        }
                    });
                }

            }
            executorService.shutdown();
            while(!executorService.isTerminated());

            long end = System.currentTimeMillis();
            System.out.println("phase:"+phases[i]);
            System.out.println("total:" +(end - start)+"ms");


            List<Long> latencies = new ArrayList<>();
            long totalThrough = 0;

      for(Statistic statistic :list ){
        latencies.addAll(statistic.getLatencies());
        long res = statistic.getThroughPut();
//        System.out.println(res);
        totalThrough += res;
//          for(int l = 0; l < statistic.getLatencies().size();l++){
//              if(statistic.getLatencies().get(l) == null){
//                  System.out.println(l);
//                  System.out.println(statistic.getLatencies().get(l));
//              }
//          }
//          System.out.println(statistic.getLatencies().size());
      }
            Collections.sort(latencies);
            System.out.println("99th latency:"+latencies.get((int)(latencies.size()*0.99))+"ms");
            System.out.println("95th latency:"+latencies.get((int)(latencies.size()*0.95))+"ms");
            System.out.println("throughput:" + totalThrough/((end-start)/1000));

            System.out.println("=======================");

        }


//        while(!executorService.isTerminated());
        client.close();

    }

}
