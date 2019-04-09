package com.tegongdete.luckyme.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tegongdete.luckyme.bean.Order;
import com.tegongdete.luckyme.util.ProtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class OrderProduceService {
    private static final Logger logger = LoggerFactory.getLogger(OrderProduceService.class);
    private static final ConnectionFactory connectionFactory = new ConnectionFactory();
    private static final String QUEUE_NAME = "luckyme_order";

    public OrderProduceService() {
        init();
    }

    private void init() {
        connectionFactory.setHost("localhost");
    }

    public int produceOrder(Order order) {
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, ProtoUtil.getBytes(order));
            channel.close();
            connection.close();
            return 1;
        }
        catch (Exception e)  {
            logger.error("Failed to publish msg:" +  e.getMessage());
        }
        return 0;
    }
}
