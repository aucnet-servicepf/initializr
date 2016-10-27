package ${packageName}.${artifactId}.business.service;

import ${packageName}.${artifactId}.business.domain.Product;
import ${packageName}.${artifactId}.business.repository.ProductMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {

<% if(hasMybatis) { %>
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product findProductByName(String name) {
        return productMapper.findByName(name);
    }
<% } else {%>
    @Override
    public Product findProductByName(String name) {
        return null;
    }
<%}%>

}