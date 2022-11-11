package com.hugmount.helloboot.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: Li Huiming
 * @Date: 2020/9/15
 */

@Slf4j
@Configuration
public class DateFormatConfig {

    @Getter
    public enum FormatEnum {
        FMT_1("yyyy-MM-dd", "^\\d{4}-\\d{1,2}-\\d{1,2}$")
        //
        , FMT_2("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")
        //
        , FMT_3("yyyy-MM-dd HH:mm:ss", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");

        private String fmt;
        private String reg;

        FormatEnum(String fmt, String reg) {
            this.fmt = fmt;
            this.reg = reg;
        }

    }

    /**
     * 处理后端返回的date自动转成str
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        log.info("设置统一日志格式");
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置日期格式
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat smt = new SimpleDateFormat(FormatEnum.FMT_3.getFmt());
        objectMapper.setDateFormat(smt);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        messageConverter.setObjectMapper(objectMapper);
        //设置中文编码格式
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        messageConverter.setSupportedMediaTypes(list);
        return messageConverter;
    }

    /**
     * 处理前端传入的时间字符串
     *
     * @param dateConverter
     * @return
     */
    @Bean
    public ConversionService getConversionService(DateConverter dateConverter) {
        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        Set<Converter> converters = new HashSet<>();
        converters.add(dateConverter);
        factoryBean.setConverters(converters);
        return factoryBean.getObject();
    }

    /**
     * 注入时间转换器
     *
     * @return
     */
    @Bean
    public DateConverter dateConverter() {
        return new DateConverter();
    }

    /**
     * 自定义时间转换器
     */
    public static class DateConverter implements Converter<String, Date> {
        @Override
        public Date convert(String str) {
            if (StringUtils.isEmpty(str)) {
                return null;
            }
            for (FormatEnum formatEnum : FormatEnum.values()) {
                if (str.matches(formatEnum.getReg())) {
                    return parseDate(str, formatEnum.getFmt());
                }
            }
            throw new IllegalArgumentException("日期参数有误: " + str);
        }

        static Date parseDate(String dateStr, String format) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(format);
                return dateFormat.parse(dateStr);
            } catch (Exception e) {
                log.info("请求日期参数有误: {}", dateStr);
            }
            throw new IllegalArgumentException("参数有误: " + dateStr);
        }

    }

}
