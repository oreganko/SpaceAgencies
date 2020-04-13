package com;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Carrier {

    public static void main(String[] args) throws Exception {
        // info
        System.out.println("CARRIER");
        List<String> keys = new LinkedList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your name: ");
        String carrierName = br.readLine();
        System.out.println("Enter first service: ");
        String key = br.readLine();
        keys.add(key);
        System.out.println("Enter second service: ");
        key = br.readLine();
        keys.add(key);

        for (String inKey: keys) {
            SharedTopicListener listener = new SharedTopicListener(inKey, "orders", carrierName);
            listener.run();
        }
        PrivateTopicListener adminListener = new PrivateTopicListener("#.carriers", "admin");
        adminListener.run();

    }
}
