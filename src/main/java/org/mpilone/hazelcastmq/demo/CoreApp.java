package org.mpilone.hazelcastmq.demo;

import org.mpilone.hazelcastmq.core.*;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;

/**
 * Demo of using just HazelcastMQ Core for sending and receiving messages.
 *
 * @author mpilone
 */
public class CoreApp extends AbstractApp {

  public static void main(String[] args) {
    CoreApp app = new CoreApp();
    app.runApp();
  }
  private HazelcastMQContext mqContext;
  private HazelcastMQConsumer consumer;
  private HazelcastMQInstance mqInstance;
  private HazelcastInstance hzInstance;

  @Override
  protected void start() {
    // Create a Hazelcast client.
    Config config = new Config();
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    hzInstance = Hazelcast.newHazelcastInstance(config);

    // Create a HazelcastMQ instance.
    HazelcastMQConfig mqConfig = new HazelcastMQConfig();
    mqConfig.setHazelcastInstance(hzInstance);
    mqInstance = HazelcastMQ.newHazelcastMQInstance(mqConfig);

    // Create a context for producing and consuming messages.
    mqContext = mqInstance.createContext();

    // Create a consumer on the topic and add a message listener to handle
    // incoming messages.
    consumer = mqContext.createConsumer("/topic/presentation.demo");
    consumer.setMessageListener(new HazelcastMQMessageListener() {
      public void onMessage(HazelcastMQMessage msg) {
        System.out.
            printf("%s got message %s\n", CoreApp.class.getSimpleName(),
                msg.getBodyAsString());
      }
    });

    // Create a producer and send a hello message to the topic.
    HazelcastMQProducer producer = mqContext.createProducer(
        "/topic/presentation.demo");
    producer.send("Hello world from the " + CoreApp.class.getSimpleName());
  }

  @Override
  protected void stop() {
    consumer.close();
    mqContext.close();
    mqInstance.shutdown();
    hzInstance.shutdown();
  }

}
