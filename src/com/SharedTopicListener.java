package com;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SharedTopicListener implements Runnable {
    protected String EXCHANGE_NAME;
    protected String key;
    protected Consumer consumer;
    protected String listenerName;

    public SharedTopicListener(String key, String EXCHANGE_NAME, String listenerName){
        this.key = key;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
        this.listenerName = listenerName;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(this.key, true, false, false, null);
            channel.queueBind(this.key, EXCHANGE_NAME, EXCHANGE_NAME + "." + key);
            channel.basicQos(1);

            this.consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received message: " + message);

                    int timeToSleep = 3;
                    try {
                        Thread.sleep(timeToSleep * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String name = message.split("\\[")[0];
                    String response = message + " done by: " + listenerName;
                    channel.basicPublish(EXCHANGE_NAME, "orders.agency." + name, null, response.getBytes("UTF-8"));
                }
            };

            channel.basicConsume(this.key, true, consumer);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}