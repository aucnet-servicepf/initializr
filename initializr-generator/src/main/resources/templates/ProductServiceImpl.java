package ${packageName}.business.service;

import ${packageName}.business.domain.Product;
import ${packageName}.business.domain.ProductExample;
import ${packageName}.business.repository.ProductMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {

<% if(hasMybatis) { %>
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product findProductByName(String name) {
        ProductExample condition = new ProductExample();
        condition.createCriteria().andNameEqualTo(name);

        return productMapper.selectByExample(condition).stream().findFirst().get();
    }

    @Override
    public Product findById(int id) {
        return productMapper.selectByPrimaryKey(id);
    }

<% } else {%>
    @Override
    public Product findProductByName(String name) {
        return null;
    }
<%}%>

}