package com.tegongdete.luckyme.service;

import com.tegongdete.luckyme.bean.Order;
import com.tegongdete.luckyme.bean.Stock;
import com.tegongdete.luckyme.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private StockService stockService;

    private OrderDao orderDao;

    public OrderService(StockService stockService, OrderDao orderDao) {
        this.stockService = stockService;
        this.orderDao = orderDao;
    }

    public int tryCreateOrder(int stockId) {
        Stock stock = stockService.getStock(stockId);
        if (stock.getCount() > 0) {
            System.out.println(stock.getVersion());
            if (1 == stockService.sale(stock)) {
                createOrder(stock);
                return 1;
            }
        }
        return 0;
    }

    public void createOrder(Stock stock) {
        Order order = new Order();
        order.setName(stock.getName());
        order.setSid(stock.getId());
        order.setTimestamp(String.valueOf(System.currentTimeMillis()));
        orderDao.addOrder(order);
    }
}
