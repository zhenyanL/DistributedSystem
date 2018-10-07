package Client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MyClient implements Runnable {

  private WebTarget webTarget;
  private int numIter;
  private Statistic statistic;

  public MyClient(WebTarget webTarget,int numIter, Statistic statistic){
    this.webTarget = webTarget;
    this.numIter = numIter;
    this.statistic = statistic;
  }

  @Override
  public void run() {
    for(int i = 0; i < numIter; i++){

      long startGet = System.currentTimeMillis();
      String res = webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
      long endGet = System.currentTimeMillis();
      statistic.addReq();
      statistic.addLatency(endGet - startGet);
//      System.out.println(endGet);
      if(res.equals("Got it!")){
        statistic.addSucc();
      }

      long startPost = System.currentTimeMillis();
      Response response = webTarget.request(MediaType.TEXT_PLAIN)
              .post(Entity.entity("this", MediaType.TEXT_PLAIN));
      long endPost = System.currentTimeMillis();
      statistic.addReq();
      statistic.addLatency(endPost - startPost);
      if(response.getStatus() == 200){
        statistic.addSucc();
      }
      response.close();
    }
  }
}
