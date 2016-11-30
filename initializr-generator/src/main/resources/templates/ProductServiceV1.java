package ${packageName}.api.v1;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@Service
public class ProductServiceV1 {

    private RestTemplate restTemplate;

    public ProductServiceV1(
                    @LoadBalanced RestTemplate normalRestTemplate) {

        this.restTemplate = normalRestTemplate;

    }

    public String remoteInfo(String ret){

        return restTemplate.getForObject(
                        UriComponentsBuilder.fromHttpUrl("https://product/v1/products/local").pathSegment(ret).build().toUri(),
                        String.class);

    }

    @HystrixCommand(fallbackMethod = "fallbackInfo")
    public String random(String ret){

        if(System.currentTimeMillis() % 3 == 0 || "fail".equals(ret)){
            throw new RuntimeException("1/3 failed");
        }else{
            return   remoteInfo(ret);
        }
    }

    public String fallbackInfo(String ret) {

        return "fallbackInfo:" + ret + "@" + System.currentTimeMillis();
    }






}
