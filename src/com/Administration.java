package com;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Administration {
    public static void main(String[] args) throws Exception {
        // info
        System.out.println("ADMINISTRATION CONSOLE");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String ORDER_EXCHANGE_NAME = "orders";
        String ADMIN_EXCHANGE_NAME = "admin";

        channel.exchangeDeclare(ORDER_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(ADMIN_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        PrivateTopicListener orderExchangeListener = new PrivateTopicListener("#", ORDER_EXCHANGE_NAME);
        orderExchangeListener.run();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Enter your destination: <agencies|carriers|all>");
            String destination = br.readLine();
            System.out.println("Enter your message");
            String message = "[ADMIN] " + br.readLine();
            if (destination.equals("all"))
                    destination = "agencies.carriers";
            String key = "admin." + destination;

            // break condition
            if ("exit".equals(message)) {
                break;
            }

            // publish
            channel.basicPublish(ADMIN_EXCHANGE_NAME, key, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message + " to " + key);
        }
    }
}
