package com.hugmount.helloboot.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @Author： Li HuiMing
 * @Date: 2021/6/8
 */
@Slf4j
public class XMLUtil {

    public static String convertObjToXml(Object obj) {
        try {
            StringWriter sw = new StringWriter();
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw);
            return sw.toString();
        } catch (JAXBException e) {
            log.error("convertObjToXml异常", e);
        }
        return null;
    }

    public static <T> T convertXmlToObject(String xmlStr, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            Object xmlObject = unmarshaller.unmarshal(sr);
            return (T) xmlObject;
        } catch (JAXBException e) {
            log.error("convertXmlToObject异常", e);
        }
        return null;
    }

}
