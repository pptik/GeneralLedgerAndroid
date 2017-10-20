package id.pptik.org.generalledger.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import id.pptik.org.generalledger.config.Constants;
import id.pptik.org.generalledger.model.User;

/**
 * Created by Hafid on 9/27/2017.
 */

public class AMQPClient {
    public static final String TAG = "[AMQPClient]";

    private Connection connection;
    private Channel channel;
    private String exchangeName;
    private String host;
    private int port;
    private String userName;
    private String password;
    private String virtualHost;
    private boolean connected = false;
    private User userDetail;
    private Thread subscribeThread;

    public BlockingDeque<String> queue = new LinkedBlockingDeque<String>();

    public AMQPClient(User user) {
        Log.i(TAG, "Initiated!");
        exchangeName = Constants.RMQ_EXCHANGE_NAME;
        host = Constants.BASE_URL_RMQ;
        userName = Constants.RMQ_USERNAME;
        password = Constants.RMQ_PASSWORD;
        virtualHost = Constants.RMQ_VIRTUAL_HOST;
        port = Constants.PORT_RMQ;
        userDetail = user;
    }

    protected boolean createConnection() {
        if (connected) return true;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setPort(port);
        factory.setVirtualHost(virtualHost);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            connected = true;
            Log.i(TAG, "Connection Created");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
            Log.i(TAG, "Connection Failed");
            return false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            Log.i(TAG, "Connection Failed");
            connected = false;
            return false;
        }
    }

    public void addMessage(String message) throws InterruptedException {
        queue.putLast(message);
    }

    public void subscribe(final String routingKey, final Handler handler) throws IOException, InterruptedException {
        Log.i(TAG, "Create subscription message handler");
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (createConnection()) {
                        String replyQueueName = channel.queueDeclare().getQueue();
                        DefaultConsumer consumer = new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                                String messageReturn = new String(body);
                                Log.i(TAG, "REPLIED" + messageReturn);
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("result_response", messageReturn);
                                msg.setData(bundle);
                                handler.sendMessageDelayed(msg, 100);
                            }
                        };
                        while (true) {
                            if (!queue.isEmpty()) {
                                final String corrId = UUID.randomUUID().toString();
                                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                                        .replyTo(replyQueueName)
                                        .correlationId(corrId)
                                        .build();
                                final String message = queue.takeFirst();
                                Log.i(TAG, "PUBLISH " + message);
                                channel.basicPublish(exchangeName, routingKey, props, message.getBytes("UTF-8"));

                                channel.basicConsume(replyQueueName, true, consumer);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(5000); //sleep and then try again
                    } catch (InterruptedException e1) {
                        connected = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result_response", "interupted");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    connected = false;
                }
            }
        });
        subscribeThread.start();
    }

    public void close() throws IOException {
        Log.i(TAG, "Try to close connection");
        if (isConnected()) {
            connection.close();
            Log.i(TAG, "Connection closed!");
            connected = false;
        }
        if (subscribeThread != null) {
            subscribeThread.interrupt();
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

}