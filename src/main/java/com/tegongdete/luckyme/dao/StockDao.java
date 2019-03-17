package com.tegongdete.luckyme.dao;

import com.tegongdete.luckyme.bean.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockDao {
    String TABLE_NAME = "stock";
    String SELECT_FIELDS = "id , name, count, sale, version";

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where id= #{id}"})
    Stock selectById(int id);

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where id= #{id} for update"})
    Stock selectByIdForUpdate(int id);

    //@Update({"update ", TABLE_NAME, "set count=count-1, sale=sale+1 where id= #{id}"})
    int sale(Stock stock);
}
