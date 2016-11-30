package ${packageName}.api.v1;


import ${packageName}.business.domain.Product;
import ${packageName}.business.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/v1/products")
public class ProductControllerV1 {

    @Autowired
    ProductService productService;

    @Autowired
    private ProductServiceV1 productServiceV1;

    @GetMapping(value = "/{name}")
    public Product findProductByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }

    @GetMapping(value = "/random/{ret}")
    public String random(@PathVariable String ret) {
        return productServiceV1.random(ret);
    }

    @GetMapping(value = "/remote/{ret}")
    public String remote(@PathVariable String ret) {
        return productServiceV1.remoteInfo(ret);

    }
}