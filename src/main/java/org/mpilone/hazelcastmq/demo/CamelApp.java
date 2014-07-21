package org.mpilone.hazelcastmq.demo;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.mpilone.hazelcastmq.camel.*;
import org.mpilone.hazelcastmq.core.*;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author mpilone
 */
public class CamelApp extends AbstractApp {

  public static void main(String[] args) {
    CamelApp app = new CamelApp();
    app.runApp();
  }
  private HazelcastMQInstance mqInstance;
  private HazelcastInstance hzInstance;
  private DefaultCamelContext camelContext;

  @Override
  protected void start() throws Exception {
    hzInstance = HazelcastClient.newHazelcastClient();

    HazelcastMQConfig mqConfig = new HazelcastMQConfig();
    mqConfig.setHazelcastInstance(hzInstance);
    mqInstance = HazelcastMQ.
        newHazelcastMQInstance(mqConfig);

    HazelcastMQCamelConfig mqCamelConfig = new HazelcastMQCamelConfig();
    mqCamelConfig.setHazelcastMQInstance(mqInstance);
    HazelcastMQCamelComponent mqComponent = new HazelcastMQCamelComponent();
    mqComponent.setConfiguration(mqCamelConfig);

    camelContext = new DefaultCamelContext();
    camelContext.addComponent("hazelcastmq", mqComponent);

    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() {
        from("hazelcastmq:topic:presentation.demo")
            .to("stream:out");

        from("direct:demo").to("hazelcastmq:topic:presentation.demo");
      }
    });
    camelContext.start();

    ProducerTemplate producer = camelContext.createProducerTemplate();
    producer.sendBody("direct:demo", "Hello world from the camel app.");
    producer.stop();
  }

  @Override
  protected void stop() throws Exception {
    camelContext.stop();
    mqInstance.shutdown();
    hzInstance.shutdown();
  }

}
