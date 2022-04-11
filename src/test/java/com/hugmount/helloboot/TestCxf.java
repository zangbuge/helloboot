package com.hugmount.helloboot;

import com.hugmount.helloboot.cxf.client.HelloService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * @author Li Huiming
 * @date 2022/4/11
 */
public class TestCxf {
    public static void main(String[] args) {
        // WS接口地址
        String address = "http://localhost:8086/helloboot/cxf/cxfHelloService?wsdl";
        // 代理工厂
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        // 设置代理地址
        jaxWsProxyFactoryBean.setAddress(address);
        // 设置接口类型
        jaxWsProxyFactoryBean.setServiceClass(HelloService.class);
        // 创建一个代理接口实现, 客户端的这个 HelloService.class 是由ws接口生产的
        // 下载cxf工具, 选择相应的版本 https://cxf.apache.org/download.html
        // 解压apache-cxf-2.6.2.zip 进入bin目录
        // 执行wsdl2java命令
        // wsdl2java -p com -d src -all wsdlUrl
        //-p  指定其wsdl的命名空间，也就是要生成代码的包名
        //-d  指定要产生代码所在目录
        //-client 生成客户端测试web service的代码
        //-server 生成服务器启动web  service的代码
        //-impl 生成web service的实现代码
        //-ant  生成build.xml文件
        //-all 生成所有开始端点代码
        HelloService userService = (HelloService) jaxWsProxyFactoryBean.create();
        String res = userService.sayHello("lhm");
        System.out.println("调用webservice返回" + res);
    }

}
