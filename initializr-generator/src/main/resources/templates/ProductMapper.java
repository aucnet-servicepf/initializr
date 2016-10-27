package ${packageName}.${artifactId}.business.repository;

import ${packageName}.${artifactId}.business.domain.Product;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface ProductMapper {

    @Select("select * from product where name = #{name}")
    Product findByName(@Param("name") String name);

}