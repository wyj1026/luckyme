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
