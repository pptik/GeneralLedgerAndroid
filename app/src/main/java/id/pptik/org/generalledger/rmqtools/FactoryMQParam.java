package id.pptik.org.generalledger.rmqtools;

import id.pptik.org.generalledger.config.Constants;

/**
 * Created by Hafid on 10/16/2017.
 */

public class FactoryMQParam {
    private String exchangeName;
    private String host;
    private int port;
    private String replyQueueName;
    private String userName;
    private String password;
    private String virtualHost;
    private String exchangeType;

    public FactoryMQParam() {
        this.exchangeName = Constants.RMQ_EXCHANGE_NAME;
        this.host = Constants.BASE_URL_RMQ;
        this.port = Constants.PORT_RMQ;
        this.userName = Constants.RMQ_USERNAME;
        this.password = Constants.RMQ_PASSWORD;
        this.virtualHost = Constants.RMQ_VIRTUAL_HOST;
        this.exchangeType = Constants.RMQ_EXCHANGE_TYPE;
    }

    public FactoryMQParam(String exchangeName, String host, int port, String replyQueueName, String userName, String password, String virtualHost, String exchangeType) {
        this.exchangeName = exchangeName;
        this.host = host;
        this.port = port;
        this.replyQueueName = replyQueueName;
        this.userName = userName;
        this.password = password;
        this.virtualHost = virtualHost;
        this.exchangeType = exchangeType;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getReplyQueueName() {
        return replyQueueName;
    }

    public void setReplyQueueName(String replyQueueName) {
        this.replyQueueName = replyQueueName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }
}
