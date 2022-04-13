package com.hugmount.helloboot.cxf.client;

import lombok.Data;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author Li Huiming
 * @date 2022/4/11
 */
@Data
public class CxfClientProxyFactory {

    private String url;
    private String username;
    private String password;

    private JaxWsProxyFactoryBean jaxWsProxyFactoryBean = null;

    public <T> T createObject(Class<T> t) {
        if (jaxWsProxyFactoryBean != null) {
            return jaxWsProxyFactoryBean.create(t);
        }
        jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(url);
        // 设置认证方式 http base
        jaxWsProxyFactoryBean.setUsername(username);
        jaxWsProxyFactoryBean.setPassword(password);
        // 设置认证方式 自定义拦截
        AbstractPhaseInterceptor interceptor = new LoginInterceptor(username, password);
        jaxWsProxyFactoryBean.getOutInterceptors().add(interceptor);
        return jaxWsProxyFactoryBean.create(t);
    }

    public static class LoginInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

        private String username;
        private String password;

        public LoginInterceptor(String username, String password) {
            super(Phase.PREPARE_SEND);
            this.username = username;
            this.password = password;
        }

        @Override
        public void handleMessage(SoapMessage soap) throws Fault {
            Document doc = DOMUtils.createDocument();
            Element auth = doc.createElement("Authorization");
            Element username = doc.createElement("username");
            Element password = doc.createElement("password");
            username.setTextContent(this.username);
            password.setTextContent(this.password);

            auth.appendChild(username);
            auth.appendChild(password);
            List<Header> headers = soap.getHeaders();
            headers.add(0, new Header(new QName("header"), auth));
        }
    }

}
