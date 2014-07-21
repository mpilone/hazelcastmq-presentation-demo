package org.mpilone.hazelcastmq.demo;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public abstract class AbstractApp {

  protected void runApp() {

    try {
      System.setProperty("hazelcast.logging.type", "slf4j");
      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
      System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
      System.setProperty("org.slf4j.simpleLogger.log.org.mpilone.hazelcastmq",
          "info");
      System.setProperty("org.slf4j.simpleLogger.log.org.mpilone.yeti",
          "info");
      System.setProperty("org.slf4j.simpleLogger.log.com.hazelcast", "warn");
      System.setProperty("org.slf4j.simpleLogger.log.io.netty", "warn");
      System.setProperty("org.slf4j.simpleLogger.log.org.apache.camel", "warn");


      start();

      System.out.printf("App %s is running. Press any key to stop.\n",
          getClass().
          getSimpleName());

      try {
        System.in.read();
      }
      catch (IOException ex) {
        // Ignore and exit.
      }

      stop();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  protected abstract void start() throws Exception;

  protected abstract void stop() throws Exception;
}
