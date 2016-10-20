package ${packageName}.business.repository;

import jp.co.aucnet.business.domain.Product;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface ProductMapper {

    @Select("select * from product where name = #{name}")
    Product findByName(@Param("name") String name);

}