package ${packageName}.api.v1;


import java.util.List;
import java.util.stream.Collectors;

import org.dozer.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.aucnet.common.exception.ResourceNotFoundException;
import ${packageName}.business.domain.Product;
import ${packageName}.business.service.ProductService;
@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductRestControllerV1 {

    @Autowired
    ProductService productService;

    @Autowired
    private ProductServiceV1 productServiceV1;

    @Autowired
    Mapper mapper ;


    @GetMapping(value = "/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Product findProductByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }
    private ProductResource convert(Product product){
        return mapper.map(product, ProductResource.class);
    }


    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductResource> findAll(@Validated ProductResource resouce, Pageable pageable) {

        Page<Product> pageResult =  productService.pageByName(resouce.getName(), pageable);

        List<ProductResource> result =  pageResult.getContent().stream().map(product -> {
            return convert(product);
        }).collect(Collectors.toList());

        Page<ProductResource> responseResource = new PageImpl<>(result,
                        pageable, pageResult.getTotalElements());

        return responseResource;

    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResource create(@Validated @RequestBody ProductResource resouce){

        Product p  = mapper.map(resouce, Product.class);
        productService.create(p);

        return convert(p);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResource update(@PathVariable Long id, @Validated @RequestBody ProductResource resouce){

        Product p  =    productService.findById(id);

        if(p == null){
            throw new ResourceNotFoundException("product not found");
        }


        mapper.map(resouce,  p);
        p.setId(id);

        //TODO error optimistic lock error
        productService.update(p);

        return convert(p);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){

        Product p  =    productService.findById(id);

        if(p == null){
            throw new ResourceNotFoundException("product not found");
        }
        productService.delete(p);
    }
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product findProductById(@PathVariable Long id) {
        return productService.findById(id);
    }


    @GetMapping(value = "/random/{ret}")
    @ResponseStatus(HttpStatus.OK)
    public String random(@PathVariable String ret) {
        return productServiceV1.random(ret);
    }

    @GetMapping(value = "/remote/{ret}")
    @ResponseStatus(HttpStatus.OK)
    public String remote(@PathVariable String ret) {
        return productServiceV1.remoteInfo(ret);

    }
}