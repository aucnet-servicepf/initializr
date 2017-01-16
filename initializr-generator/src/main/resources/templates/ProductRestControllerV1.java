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

    private ProductService productService;

    private ProductServiceV1 productServiceV1;

    private Mapper mapper ;

    /**
     *  constructor.
     * @param mapper mapper
     * @param productService 自身のマイクロサービスのIF
     * @param productServiceV1 別のマイクロサービスIF
     */
    @Autowired
    public ProductRestControllerV1(Mapper mapper, ProductService productService,
                    ProductServiceV1 productServiceV1) {
        this.mapper = mapper;
        this.productService = productService;
        this.productServiceV1 = productServiceV1;
    }


    @GetMapping(value = "/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Product findProductByName(@PathVariable String name) {
        return productService.findProductByName(name);
    }

    private ProductResource convert(Product product) {
        return mapper.map(product, ProductResource.class);
    }


    /**
     * 一覧取得.
     * @param resouce パラメータ
     * @param pageable ページング
     * @return 一覧データ
     */
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

    /**
     * レコード作成する.
     * @param resouce パラメータ
     * @return 作成した結果
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResource create(@Validated @RequestBody ProductResource resouce) {

        Product product = mapper.map(resouce, Product.class);
        productService.create(product);

        return convert(product);
    }

    /**
     * レコード更新する.
     * @param id id
     * @param resouce パラメータ
     * @return 更新した結果
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResource update(@PathVariable Long id,
                    @Validated @RequestBody ProductResource resouce) {

        Product product = productService.findById(id);

        if (product == null) {
            //TODO should use message id
            throw new ResourceNotFoundException("product not found");
        }

        mapper.map(resouce,  product);
        product.setId(id);

        //TODO error optimistic lock error
        productService.update(product);

        return convert(product);
    }

    /**
     * レコード削除する.
     * @param id id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Product product = productService.findById(id);

        //TODO should use message id
        if (product == null) {
            throw new ResourceNotFoundException("product not found");
        }
        productService.delete(product);
    }


    /**
     * レコード取得する.
     * @param id id
     * @return 取得した結果
     */
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