package ${packageName}.business.repository;

import ${packageName}.business.domain.Product;


import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ProductMapper {

    Product findByName(String name);

    Product findById(int id);
}