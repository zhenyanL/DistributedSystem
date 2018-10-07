package Client;

import java.util.ArrayList;
import java.util.List;

public class Statistic {
  private int numReq;
  private int numSucc;
  private List<Long> latencies;

  public Statistic(){
    numReq = 0;
    numSucc = 0;
    latencies = new ArrayList<>();
  }

  public void addReq(){
    numReq++;
  }

  public void addSucc(){
    numSucc++;
  }

  public void addLatency(long time){
    latencies.add(time);
  }

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
}
