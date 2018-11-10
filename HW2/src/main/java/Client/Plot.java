package Client;
//
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtilities;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.category.DefaultCategoryDataset;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadLocalRandom;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Plot {

  private static String address = "http://HW2-620867982.us-west-2.elb.amazonaws.com:8080";
//      private static String address = "http://54.191.170.77:8080";
  //  private static String address = "https://wlcv8chfv5.execute-api.us-east-1.amazonaws.com/Prod/myresource";
  private static String port = "8080";

  private static int maxThreads = 500;

  private static int numIter = 500;

  private static double[] percent = {0.1,0.5,1,0.25};

  private static int maxDayNum = 1;

  private static int userPopulation = 100000;

  private static int maxSteps = 5000;

  private static String[] phases = {"Warmup phase","Loading phase","Peak phase","Cooldown phase"};

  private static int[][] timeIntervals = {{0,2},{3,7},{8,18},{19,23}};

  private static final String prefix = "HW2/webapi/myresource/";




  public static void main(String[] args) throws IOException {

    final Client client = ClientBuilder.newClient();


      client.target(address).path(prefix+"clear").request().delete();
      System.out.println("???");


      List<Statistic> list = new ArrayList<>();
      ExecutorService executorService = Executors.newFixedThreadPool(30);


      System.out.println(" running");


      final long start = System.currentTimeMillis();

      for (int j = 0; j < maxThreads; j++) {

        for(int t = 0; t < numIter; t++){
          final Statistic statistic = new Statistic();
          list.add(statistic);
          executorService.submit(new Runnable() {
            @Override
            public void run() {

              for (int k = 0; k < 3; k++) {
                int users = ThreadLocalRandom.current().nextInt(1, userPopulation);
                int intervals = ThreadLocalRandom.current().nextInt(timeIntervals[1][0], timeIntervals[1][1]);
                int stepCounts = ThreadLocalRandom.current().nextInt(1, maxSteps);

                long start1 = System.currentTimeMillis();
                String res = client.target(address).path(prefix + users + "/1/" + intervals + "/" + stepCounts).request(MediaType.TEXT_PLAIN)
                        .post(Entity.entity("", MediaType.TEXT_PLAIN), String.class);
                long end1 = System.currentTimeMillis();
//                                System.out.println(res);
                statistic.addPostResForPlot(new String[]{res,String.valueOf(end1-start)});
                statistic.addLatency(end1-start1);
                statistic.addPostRes(res);

                long start2 = System.currentTimeMillis();
                String res2 = client.target(address).path(prefix + "single/"+users + "/1").request(MediaType.TEXT_PLAIN)
                        .get(String.class);
//                                System.out.println("???");
//                                System.out.println(res2);
                long end2 = System.currentTimeMillis();
                statistic.addGetResForPlot(new String[]{res,String.valueOf(end1-start)});
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
      System.out.println("phase:");
      System.out.println("total:" +(end - start)+"ms");


      List<Long> latencies = new ArrayList<>();
      long totalThrough = 0;






//      List<Long> latencies = new ArrayList<>();
      List<Long> throughputTime = new ArrayList<>();
//      long totalThrough = 0;
//
//      for(Statistic statistic :list ){
//        throughputTime.addAll(statistic.getThroughPutForPlot());
//
//      }

      for(Statistic statistic :list ){
        latencies.addAll(statistic.getLatencies());
        long res = statistic.getThroughPut();
//        System.out.println(res);
        totalThrough += res;
        throughputTime.addAll(statistic.getThroughPutForPlot());
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


    Collections.sort(throughputTime);
    List<Long> res = new LinkedList<>();
    int cnt = 0;
    System.out.println(throughputTime);
    long curr = 0;
    for(int i = 0; i < throughputTime.size(); i++){
      while(i < throughputTime.size() && throughputTime.get(i) <= curr){
        cnt++;
        i++;
      }
      res.add((long)cnt);
      cnt=0;
      i--;
      curr += 2000;
    }
    res.add((long)0);
    System.out.println(res.toString());

////
////
////
    DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
//    Iterator iterator = throughputTime.iterator();
    for(int i = 0; i < res.size(); i++){
      line_chart_dataset.addValue(res.get(i),"throughput", String.valueOf(i*1000));
    }


    JFreeChart lineChartObject = ChartFactory.createLineChart(
            "througput Vs times","time",
            "throughput",
            line_chart_dataset, PlotOrientation.VERTICAL,
            true,true,false);

    int width = 640; /* Width of the image */
    int height = 480; /* Height of the image */
    File lineChart = new File( "out.jpeg" );
    try {
      ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
    } catch (IOException e) {
      e.printStackTrace();
    }




//        while(!executorService.isTerminated());
    client.close();

  }

}





//
//
//public class Plot {
//
//  private static String address = "http://localhost:8080";
//  //    private static String address = "http://13.59.96.80:8080";
//  //  private static String address = "https://wlcv8chfv5.execute-api.us-east-1.amazonaws.com/Prod/myresource";
//  private static String port = "8080";
//
//  private static int maxThreads = 2;
//
//  private static int numIter = 100;
//
//  private static double[] percent = {0.1,0.5,1,0.25};
//
//  private static int maxDayNum = 1;
//
//  private static int userPopulation = 100000;
//
//  private static int maxSteps = 5000;
//
//  private static String[] phases = {"Warmup phase","Loading phase","Peak phase","Cooldown phase"};
//
//  private static int[][] timeIntervals = {{0,2},{3,7},{8,18},{19,23}};
//
//  private static final String prefix = "HW2/webapi/myresource/";
//
//
//
//  public static void main(String[] args) throws IOException {
//
//    final Client client = ClientBuilder.newClient();
//
//
//      client.target(address).path(prefix+"clear").request().delete();
//
//
//      List<Statistic> list = new ArrayList<>();
//      ExecutorService executorService = Executors.newFixedThreadPool(30);
//
//
//
//
//
//      final long start = System.currentTimeMillis();
//      for (int j = 0; j < maxThreads; j++) {
//
//        for(int t = 0; t < numIter; t++){
//          final Statistic statistic = new Statistic();
//          list.add(statistic);
//          executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//
//              for (int k = 0; k < 3; k++) {
//                int users = ThreadLocalRandom.current().nextInt(1, userPopulation);
//                int intervals = ThreadLocalRandom.current().nextInt(timeIntervals[1][0], timeIntervals[1][1]);
//                int stepCounts = ThreadLocalRandom.current().nextInt(1, maxSteps);
//
//                long start1 = System.currentTimeMillis();
//                String res = client.target(address).path(prefix + users + "/1/" + intervals + "/" + stepCounts).request(MediaType.TEXT_PLAIN)
//                        .post(Entity.entity("", MediaType.TEXT_PLAIN), String.class);
//                long end1 = System.currentTimeMillis();
////                                System.out.println(res);
//                statistic.addGetResForPlot(new String[]{res,String.valueOf(end1-start)});
//                statistic.addLatency(end1-start1);
//
////                statistic.addPostRes(res);
//
//                if(k == 0 || k == 1){
//                  long start2 = System.currentTimeMillis();
//                  String res2 = client.target(address).path(prefix + "single/"+users + "/1").request(MediaType.TEXT_PLAIN)
//                          .get(String.class);
////                                System.out.println("???");
////                                System.out.println(res2);
//                  long end2 = System.currentTimeMillis();
//                  statistic.addGetResForPlot(new String[]{res,String.valueOf(end2-start)});
//                  statistic.addLatency(end2-start2);
////                  statistic.addGetRes(res2);
//                }
//
//              }
//
//
//            }
//          });
//        }
//
//      }
//      executorService.shutdown();
//      while(!executorService.isTerminated());
//
//      long end = System.currentTimeMillis();
//
//      System.out.println("total:" +(end - start)+"ms");
//
//
//      List<Long> latencies = new ArrayList<>();
//      List<Long> throughputTime = new ArrayList<>();
//      long totalThrough = 0;
//
//      for(Statistic statistic :list ){
//        throughputTime.addAll(statistic.getThroughPutForPlot());
//
//      }
//
//      System.out.println("99th latency:"+latencies.get((int)(latencies.size()*0.99))+"ms");
//      System.out.println("95th latency:"+latencies.get((int)(latencies.size()*0.95))+"ms");
//      System.out.println("throughput:" + totalThrough/((end-start)/1000));
//
//      System.out.println("=======================");
//
//      Collections.sort(throughputTime);
//
//
//
//    DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
//    Iterator iterator = throughputTime.iterator();
//    for(int i = 0; i < throughputTime.size(); i++){
//      line_chart_dataset.addValue(i+1,"throughput",String.valueOf(iterator.next()));
//    }
//
//
//    JFreeChart lineChartObject = ChartFactory.createLineChart(
//            "througput Vs times","time",
//            "throughput",
//            line_chart_dataset, PlotOrientation.VERTICAL,
//            true,true,false);
//
//    int width = 640; /* Width of the image */
//    int height = 480; /* Height of the image */
//    File lineChart = new File( "out.jpeg" );
//    try {
//      ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//
////        while(!executorService.isTerminated());
//    client.close();
//
//  }
//
//}
