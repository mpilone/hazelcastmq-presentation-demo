package org.mpilone.hazelcastmq.demo;

import org.mpilone.hazelcastmq.core.*;

import com.hazelcast.core.*;

/**
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

    hzInstance = Hazelcast.newHazelcastInstance();

    HazelcastMQConfig mqConfig = new HazelcastMQConfig();
    mqConfig.setHazelcastInstance(hzInstance);
    mqInstance = HazelcastMQ.
        newHazelcastMQInstance(mqConfig);

    mqContext = mqInstance.createContext();

    consumer = mqContext.createConsumer(
        "/topic/presentation.demo");
    consumer.setMessageListener(new HazelcastMQMessageListener() {
      public void onMessage(HazelcastMQMessage msg) {
        System.out.printf("%s got message %s\n",
            CoreApp.class.getSimpleName(), msg.getBodyAsString());
      }
    });

    HazelcastMQProducer producer = mqContext.createProducer();
    producer.send("/topic/presentation.demo",
        "Hello world from the core app demo.");
  }

  @Override
  protected void stop() {
    consumer.close();
    mqContext.close();
    mqInstance.shutdown();
    hzInstance.shutdown();
  }

}
