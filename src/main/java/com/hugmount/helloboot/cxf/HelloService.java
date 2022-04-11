package com.hugmount.helloboot.cxf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author Li Huiming
 * @date 2022/4/11
 */

@WebService
public interface HelloService {

    @WebMethod
    String sayHello(@WebParam String msg);

}
