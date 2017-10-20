package id.pptik.org.generalledger.rmqtools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Base class for objects that connect to a RabbitMQ Broker
 */
public abstract class IConnectToRabbitMQ {


    protected Channel mModel = null;
    protected Connection mConnection;

    protected boolean Running;

    FactoryMQParam factoryMQParam;

    public IConnectToRabbitMQ(FactoryMQParam factoryMQParam) {
        this.factoryMQParam = factoryMQParam;
    }

    public void Dispose() {
        Running = false;

        try {
            if (mConnection != null)
                mConnection.close();
            if (mModel != null)
                mModel.abort();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Connect to the broker and create the exchange
     *
     * @return success
     */
    public boolean connectToRabbitMQ() {
        if (mModel != null && mModel.isOpen())//already declared
            return true;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(factoryMQParam.getHost());
            factory.setUsername(factoryMQParam.getUserName());
            factory.setPassword(factoryMQParam.getPassword());
            factory.setPort(factoryMQParam.getPort());
            factory.setVirtualHost(factoryMQParam.getVirtualHost());
            mConnection = factory.newConnection();
            mModel = mConnection.createChannel();
            mModel.exchangeDeclare(factoryMQParam.getExchangeName(), factoryMQParam.getExchangeType(), true);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}