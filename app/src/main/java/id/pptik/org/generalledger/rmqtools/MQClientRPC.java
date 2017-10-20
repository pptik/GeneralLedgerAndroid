package id.pptik.org.generalledger.rmqtools;

/**
 * Created by Hafid on 10/16/2017.
 */

public class MQClientRPC extends IConnectToRabbitMQ {

    public MQClientRPC(FactoryMQParam factoryMQParam) {
        super(factoryMQParam);
    }

    private OnReceiveMessageHandler onReceiveMessageHandler;

    public interface OnReceiveMessageHandler{
        public void onReceiveMessage(byte[] message);
    };

    public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler){
        onReceiveMessageHandler = handler;
    };

    private void Produce(){
        Thread thread = new Thread(){
            @Override
            public void run(){
                while (Running){
                    
                }
            }
        };
    }
}
