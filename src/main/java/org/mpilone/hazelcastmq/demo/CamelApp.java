package org.mpilone.hazelcastmq.demo;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.mpilone.hazelcastmq.camel.*;
import org.mpilone.hazelcastmq.core.*;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

/**
 * Demo of using just HazelcastMQ Camel for sending and receiving messages.
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
    // Create a Hazelcast client.
    hzInstance = HazelcastClient.newHazelcastClient();

    // Create a HazelcastMQ instance.
    HazelcastMQConfig mqConfig = new HazelcastMQConfig();
    mqConfig.setHazelcastInstance(hzInstance);
    mqInstance = HazelcastMQ.
        newHazelcastMQInstance(mqConfig);

    // Create a HazelcastMQ Camel component.
    HazelcastMQCamelConfig mqCamelConfig = new HazelcastMQCamelConfig();
    mqCamelConfig.setHazelcastMQInstance(mqInstance);
    HazelcastMQCamelComponent mqComponent = new HazelcastMQCamelComponent();
    mqComponent.setConfiguration(mqCamelConfig);

    // Create the Camel context and register the component.
    camelContext = new DefaultCamelContext();
    camelContext.addComponent("hazelcastmq", mqComponent);

    // Build the Camel routes. The first route will read from a HazelcastMQ
    // topic and send the exchange to stdout. The second route will read from
    // the direct endpoint and send the message to the HazelcastMQ topic.
    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() {
        from("hazelcastmq:topic:presentation.demo")
            .to("stream:out");

        from("direct:demo").to("hazelcastmq:topic:presentation.demo");
      }
    });
    camelContext.start();

    // Create a Camel producer that will send an exchange to the direct 
    // endpoint and then send the hello message.
    ProducerTemplate producer = camelContext.createProducerTemplate();
    producer.sendBody("direct:demo", "Hello world from the " + CamelApp.class.
        getSimpleName());
    producer.stop();
  }

  @Override
  protected void stop() throws Exception {
    camelContext.stop();
    mqInstance.shutdown();
    hzInstance.shutdown();
  }

}
