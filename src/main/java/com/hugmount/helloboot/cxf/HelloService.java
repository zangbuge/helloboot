package com.hugmount.helloboot.cxf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 提供WebService服务
 *
 * @author Li Huiming
 * @date 2022/4/11
 */

@WebService
public interface HelloService {

    @WebMethod
    String sayHello(@WebParam String msg);

}
