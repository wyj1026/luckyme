package com.tegongdete.luckyme.dao;

import com.tegongdete.luckyme.bean.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao {
    String TABLE_NAME = "stock_order";
    String INSERT_FIELDS = "sid, name, timestamp";

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{sid}, #{name}, #{timestamp})"})
    int addOrder(Order order);
}
