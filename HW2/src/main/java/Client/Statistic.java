package Client;

import java.util.ArrayList;
import java.util.List;

public class Statistic {
  private int numReq;
  private int numSucc;
  private List<Long> latencies;
  private List<String> postRes;
  private List<String> getRes;

  public Statistic(){
    numReq = 0;
    numSucc = 0;
    latencies = new ArrayList<>();
    postRes = new ArrayList<>();
    getRes = new ArrayList<>();
  }

  public void addReq(){
    numReq++;
  }

  public void addSucc(){
    numSucc++;
  }

  public void addLatency(long time){
    latencies.add(time);
//    System.out.println(time);
//    System.out.println(latencies == null);
  }

  public void addPostRes(String res){ postRes.add(res);}

  public void addGetRes(String res){ getRes.add(res);}

  public int getNumReq() {
    return numReq;
  }

  public void setNumReq(int numReq) {
    this.numReq = numReq;
  }

  public int getNumSucc() {
    return numSucc;
  }

  public void setNumSucc(int numSucc) {
    this.numSucc = numSucc;
  }

  public List<Long> getLatencies() {
    return latencies;
  }

  public void setLatencies(List<Long> latencies) {
    this.latencies = latencies;
  }

  public long getThroughPut(){
    long cnt = 0;
    for(String res : postRes){
//      System.out.println(res);
//      System.out.println(res.equals("OK!"));
      if(res != null && res.equals("OK!")){
        cnt++;
      }
    }
    for(String res : getRes){
//      System.out.println(res);
      try{
        int num = Integer.parseInt(res);
        cnt++;
      }
      catch(Exception e){}
    }
//    System.out.println(cnt);
    return cnt;
  }
}
