package Client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadRequests {
//  private static String address = "http://18.188.123.15:";
  private static String address = "https://wlcv8chfv5.execute-api.us-east-1.amazonaws.com/Prod/myresource";
  private static String port = "8080";
  private static int maxThreads = 20;
  private static int numIter = 100;
  private static double[] percent = {0.1,0.5,1,0.25};
  private static String[] phases = {"Warmup phase","Loading phase","Peak phase","Cooldown phase"};

  public static void main(String[] args){
    if(args.length > 0){
      address = args[0];
      port = args[1];
      maxThreads = Integer.parseInt(args[2]);
      numIter = Integer.parseInt(args[3]);
    }

    System.out.println("client "+maxThreads+" "+numIter+" "+address +" "+port);
    int totalReq = 0;
    int totalSucc = 0;
    long totalTime = 0;

    for(int i = 0; i < percent.length; i++){
      int numThreads = (int)(maxThreads*percent[i]);

      System.out.println(phases[i] +" running");
      ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

      Client client = ClientBuilder.newClient();
//      WebTarget webTarget = client.target(address+port).path("homework1/webapi/myresource");
      WebTarget webTarget = client.target(address);
      List<Statistic> statistics = new ArrayList<>();

      long start = System.currentTimeMillis();
      for(int j = 0; j < numThreads; j++){
        Statistic statistic =new Statistic();
        MyClient myClient = new MyClient(webTarget,numIter,statistic);
        executorService.submit(myClient);
        statistics.add(statistic);
      }


      executorService.shutdown();
      while(!executorService.isTerminated());
      client.close();
      long end = System.currentTimeMillis();

      int numReq = 0;
      int numSucc = 0;
      List<Long> latencies = new ArrayList<>();
      for(Statistic statistic : statistics){
        numReq += statistic.getNumReq();
        numSucc += statistic.getNumSucc();
        latencies.addAll(statistic.getLatencies());
      }
      totalReq += numReq;
      totalSucc += numSucc;
      Collections.sort(latencies);
      long wallTime = end - start;
      totalTime += wallTime;
      long sum = 0l;
      for(long latency : latencies){
        sum+= latency;
      }
      float throughput = numReq/(float)(wallTime/1000);
      float mean = sum/(float)(latencies.size());
      float median =
              latencies.size()%2 == 0? (latencies.get((latencies.size()-1)/2) + latencies.get((latencies.size()-1)/2+1))/2 : latencies.get((latencies.size()-1)/2);


      System.out.println(phases[i]+ " percent:" + percent[i]+" completed.");
      System.out.println("Wall time:" + wallTime);
      System.out.println("total requests:"+ numReq);
      System.out.println("success requests:"+ numSucc);
      System.out.println("throughput (num of requests/wall time):" + throughput);
      System.out.println("mean latency:"+mean);
      System.out.println("median latency:"+median);
      System.out.println("99th latency:"+latencies.get((int)(latencies.size()*0.99)));
      System.out.println("95th latency:"+latencies.get((int)(latencies.size()*0.95)));
      System.out.println();

    }

    System.out.println("===========");
    System.out.println("total requests:"+totalReq);
    System.out.println("success requests:"+totalSucc);
    System.out.println("total time:"+ totalTime);

  }
}
