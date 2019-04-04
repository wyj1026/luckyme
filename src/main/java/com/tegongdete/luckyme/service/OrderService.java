package com.tegongdete.luckyme.service;

import com.tegongdete.luckyme.bean.Order;
import com.tegongdete.luckyme.bean.Stock;
import com.tegongdete.luckyme.dao.OrderDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private StockService stockService;

    private OrderDao orderDao;

    private OrderProduceService orderProduceService;

    public OrderService(StockService stockService, OrderDao orderDao, OrderProduceService orderProduceService) {
        this.stockService = stockService;
        this.orderDao = orderDao;
        this.orderProduceService = orderProduceService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int tryCreateOrder(int stockId) throws Exception{
        Stock stock = stockService.getStockForUpdate(stockId);
        if (stock.getCount() <= stock.getSale()) {
            throw new Exception("Out of stock!");
        }
        else {
            if (1 == stockService.sale(stock)) {
                return createOrder(stock);
            }
        }
        return 0;
    }

    public int createOrder(Stock stock) {
        Order order = new Order();
        order.setName(stock.getName());
        order.setSid(stock.getId());
        order.setTimestamp(String.valueOf(System.currentTimeMillis()));
        return orderProduceService.produceOrder(order);
        //return orderDao.addOrder(order);
    }
}
