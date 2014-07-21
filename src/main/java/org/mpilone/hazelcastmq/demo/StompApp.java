
package org.mpilone.hazelcastmq.demo;

import org.mpilone.hazelcastmq.core.*;
import org.mpilone.hazelcastmq.stomp.server.*;
import org.mpilone.yeti.Frame;
import org.mpilone.yeti.FrameBuilder;
import org.mpilone.yeti.client.StompClient;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author mpilone
 */
public class StompApp extends AbstractApp {

  public static void main(String[] args) {
    StompApp app = new StompApp();
    app.runApp();
  }
  private StompClient client;
  private HazelcastMQStompInstance mqStomp;
  private HazelcastMQInstance mqInstance;
  private HazelcastInstance hzInstance;

  @Override
  protected void start() throws InterruptedException {
     hzInstance = HazelcastClient.newHazelcastClient();

    HazelcastMQConfig mqConfig = new HazelcastMQConfig();
    mqConfig.setHazelcastInstance(hzInstance);
     mqInstance = HazelcastMQ.
        newHazelcastMQInstance(mqConfig);

    HazelcastMQStompConfig stompConfig = new HazelcastMQStompConfig();
    stompConfig.setHazelcastMQInstance(mqInstance);
    stompConfig.setPort(9001);
     mqStomp = HazelcastMQStomp.
        newHazelcastMQStompInstance(stompConfig);

    client = new StompClient("localhost", 9001);
    client.connect();
    client.subscribe(FrameBuilder.subscribe("/topic/presentation.demo", "1").
        build(), new StompClient.FrameListener() {
          public void frameReceived(Frame frame) throws Exception {
            System.out.printf("%s got message %s\n",
                StompApp.class.getSimpleName(), frame.getBodyAsString());
          }
        });

    client.send(FrameBuilder.send("/topic/presentation.demo",
        "Hello world from the stomp app.").build());
  }

  @Override
  protected void stop() throws InterruptedException {
      client.disconnect();
      mqStomp.shutdown();
      mqInstance.shutdown();
      hzInstance.shutdown();
  }

}
