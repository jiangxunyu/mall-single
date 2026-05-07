package com.mall.mapper;

import com.mall.po.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ProductMapper {

    Product selectById(Long id);

    List<Product> selectAll();

    void updateStock(Long id, Integer stock);

    void insert(Product product);

    @Update("""
            UPDATE product 
            SET stock = stock - #{count}
            WHERE id = #{productId} 
            AND stock >= #{count}
            """)
    int deductStock(@Param("productId") Long productId,
                    @Param("count") Integer count);

    List<Product> searchByName(String name);

    List<Product> searchByNameWithPage(@Param("name") String name, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countAll();

    Integer countByName(String name);

    List<Product> selectAllWithPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

}