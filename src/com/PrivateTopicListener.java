//This is reserved for agencies and administration only as they do not react for messages

package com;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PrivateTopicListener implements Runnable {
    private final String EXCHANGE_NAME;
    private String key;

    public PrivateTopicListener(String key, String EXCHANGE_NAME){
        this.key = key;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            // queue & bind
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, EXCHANGE_NAME + "." + key);

            // consumer (message handling)
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received: " + message);
                }
            };
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
