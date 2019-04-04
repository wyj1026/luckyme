[ 源码github地址](https://github.com/wyj1026/luckyme)
# 一、基本流程与实现
首先一个秒杀业务仍采用经典的web设计方法，即web层、service层和dao层，在不考虑任何并发的情况下，很容易得出基本的设计方案：
  **用户发送抢购请求---对应controller相应---调用serivce查看mysql库存，有则成功并减库存创建订单，没有则抢购失败**
## 1.sql表设计
* 商品库存销售表
```
use test;
drop table `stock`;
CREATE TABLE `stock` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `count` int(11) NOT NULL COMMENT '库存',
  `sale` int(11) NOT NULL COMMENT '已售',
  `version` int(11) NOT NULL COMMENT '乐观锁，版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO stock (id, name, count, sale, version) VALUES (
  1, 'iphone', 100, 0, 0);
```

* 订单表(用户信息待完善)
```
use test;
drop table `stock_order`;
CREATE TABLE `stock_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `sid` int(11) NOT NULL COMMENT '库存ID',
  `name` varchar(30) NOT NULL DEFAULT '' COMMENT '商品名称',
  `timestamp` varchar(16) NOT NULL DEFAULT '' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

* 用户表(待完善)
```
```
## 2.controller
controller逻辑很简单，根据用户请求购买的商品创建订单。成功返回1，不成功返回0.
```
package com.tegongdete.luckyme.controller;

import com.tegongdete.luckyme.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LuckyMeController {
    private static Logger logger = LoggerFactory.getLogger(LuckyMeController.class);

    @Autowired
    OrderService orderService;

    @RequestMapping(path = {"/lucky"})
    @ResponseBody
    public String index(int st, int usr) {
        logger.info(String.format("User %d try to buy No.%d, GOOD LUCK!", usr, st));
        int orderId = 0;
        try {
            orderId = orderService.tryCreateOrder(st);
        }
        catch (Exception e) {
            logger.error(e.toString());
        }
        return String.valueOf(orderId);
    }
}

```
## 3.service
* StockService 很简单，目前只需要根据id获取库存以及销售stock的功能。
```
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

```
* OrderService 也很简单，tryCreateOrder首先查库存，无库存抛出异常，有则尝试创建订单。
```
package com.tegongdete.luckyme.service;

import com.tegongdete.luckyme.bean.Order;
import com.tegongdete.luckyme.bean.Stock;
import com.tegongdete.luckyme.dao.OrderDao;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private StockService stockService;

    private OrderDao orderDao;

    public OrderService(StockService stockService, OrderDao orderDao) {
        this.stockService = stockService;
        this.orderDao = orderDao;
    }

    public int tryCreateOrder(int stockId) throws Exception{
        Stock stock = stockService.getStock(stockId);
        if (stock.getCount() <= stock.getSale()) {
            throw new Exception("Out of stock!");
        }
        else {
            if (1 == stockService.sale(stock)) {
                createOrder(stock);
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
        return orderDao.addOrder(order);
    }
}

```
## 4.dao
dao实现比较简单，先不贴了，之后加锁再说。
## 5.测试
如上面sql语句设置，可以抢购10台iphone。使用jmeter进行测试，假设有500人参与，每人重复点击3次：
![jmeter测试结果](https://upload-images.jianshu.io/upload_images/15185074-8bbb71c15dedef9f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
感觉还不错，没有出现服务崩溃的情况。
看一下数据库：
![数据库](https://upload-images.jianshu.io/upload_images/15185074-4c530e08887aefff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
看到产生了394条订单，sale达到了197，亏出裤衩。

## 6.为什么会出现这个问题？
仔细观察逻辑，发现任意请求到达时候都会先请求查看库存，如果满足条件即小于抢购量才会更新销售的数量，然后生成订单，问题在于t时刻大家一起查，发现确实还没卖完，然后大家一起下单，导致了超卖。
# 二、解决超卖
## 1.事务与隔离级别
mysql默认的事务隔离级别是可重复读，从词意上看一个事物不同时刻读取的结果应该是相同的，那么给```tryCreateOrder```方法加上事务应该能保证在```sale```的时候库存还是那么多，那么是不是就没问题呢，我们尝试开启事务：
```
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int tryCreateOrder(int stockId) throws Exception{
        Stock stock = stockService.getStock(stockId);
        if (stock.getCount() <= stock.getSale()) {
            throw new Exception("Out of stock!");
        }
        else {
            if (1 == stockService.sale(stock)) {
                createOrder(stock);
                return createOrder(stock);
            }
        }
        return 0;
    }
```
使用jmeter测试，jmeter耗时和上面差不多，不贴了，看一下mysql的结果：
![开启事务mysql结果](https://upload-images.jianshu.io/upload_images/15185074-774dd44eb9817a0d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到虽然没有狂卖400台iphone，但是还是有超卖的情况，这是为什么呢？
其实可重复读的事务隔离机制是**由 mysql 缓存了 SELECT 的结果集，保证在这个事务里同样的 SELECT 语句得到的结果始终一致。并不是在相应的行上加锁。**那么也就可以理解为什么还是会超卖了，有一个问题不明白的是，为什么超卖现象减轻了？读者明白的话可以发表一下见解，感谢🙏！
**另外，使用postgresql数据库在可重复读的隔离级别下是不会发生该问题的！**
## 2.读写锁（悲观锁）
在数据库课程上我们都有学到，一般数据库都是支持读写锁的，那么能否用读写锁来解决这个问题呢？下面尝试一下，使用```getStockForUpdate```给```select```语句加上```for update```的写锁：
```
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int tryCreateOrder(int stockId) throws Exception{
        Stock stock = stockService.getStockForUpdate(stockId);
        if (stock.getCount() <= stock.getSale()) {
            throw new Exception("Out of stock!");
        }
        else {
            if (1 == stockService.sale(stock)) {
                createOrder(stock);
                return createOrder(stock);
            }
        }
        return 0;
    }
```
使用jmeter测试，jmeter耗时和上面差不多，看一下mysql的结果：
![mysql写锁测试结果](https://upload-images.jianshu.io/upload_images/15185074-7a51ea132b309543.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
当当当，结果终于正确了！
## 3. 乐观锁
可以看到上面的sql表在设计的时候，有一个字段是version，这是为了实现乐观锁所保留的，那么既然悲观锁能解决问题，乐观锁当然也可以：
把```update```库存的语句改为：
```
update stock set sale=sale+1, version=version+1 where id={id} and version={version}
```
就 OK啦，结果也是正确的，耗时相似。