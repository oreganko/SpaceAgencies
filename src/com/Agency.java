package com;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Agency {

    public static void main(String[] args) throws Exception {
        // info
        System.out.println("SPACE AGENCY");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String ORDER_EXCHANGE_NAME = "orders";
        channel.exchangeDeclare(ORDER_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your agency name");
        String name = br.readLine();
        String message;
        String key;
        int order_no = 1;

        PrivateTopicListener listener = new PrivateTopicListener("agency." + name, ORDER_EXCHANGE_NAME);
        listener.run();
        PrivateTopicListener adminListener = new PrivateTopicListener("agencies.#", "admin");
        adminListener.run();

        while (true) {
            System.out.println("Enter your order: <people|cargo|satellite>");
            String order = br.readLine();
            message = name + "[" + order + "-" + order_no + "]";
            key = "orders." + order;
            order_no += 1;
                        // break condition
            if ("exit".equals(message)) {
                break;
            }

            // publish
            channel.basicPublish(ORDER_EXCHANGE_NAME, key, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }
    }
}
