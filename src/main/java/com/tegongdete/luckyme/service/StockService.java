package com.tegongdete.luckyme.service;

import com.tegongdete.luckyme.bean.Stock;
import com.tegongdete.luckyme.dao.StockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private StockDao stockDao;

    public StockService(StockDao stockDao) {
        this.stockDao = stockDao;
    }

    public Stock getStock(int id) {
        return stockDao.selectById(id);
    }

    public int sale(Stock stock) {
        return stockDao.sale(stock);
    }
}
