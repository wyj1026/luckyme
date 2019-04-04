package com.tegongdete.luckyme.consumer;

import com.rabbitmq.client.*;
import com.tegongdete.luckyme.bean.Order;
import com.tegongdete.luckyme.dao.OrderDao;
import com.tegongdete.luckyme.util.ProtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Component
public class OrderConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);
    private static final ConnectionFactory connectionFactory = new ConnectionFactory();
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final String QUEUE_NAME = "luckyme_order";

    private OrderDao orderDao;

    private OrderConsumer(OrderDao orderDao) {
        this.orderDao = orderDao;
        connectionFactory.setHost("localhost");
        executorService.scheduleAtFixedRate(this::consumeOrder, 1, 2, TimeUnit.SECONDS);
    }

    private void consumeOrder() {
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            GetResponse response = channel.basicGet(QUEUE_NAME, true);
            if (response != null) {
                String message = new String(response.getBody(), "UTF-8");
                Order order = new Order();
                ProtoUtil.getObj(message.getBytes(), order);
                orderDao.addOrder(order);
            }
            channel.close();
            connection.close();
        }
        catch (Exception e)  {
            logger.error("Failed to get rabbitmq connection! msg:" +  e.getMessage());
            e.printStackTrace();
        }
    }
}
