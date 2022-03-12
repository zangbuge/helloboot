package com.hugmount.helloboot.util;

import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取yml文件值
 *
 * @Author: Li Huiming
 * @Date: 2019/3/23
 */
public class YmlUtil {

    //默认文件配置文件名称
    private static String bootstrap_file = "application.yml";

    private static Map<String, String> result = new HashMap<>();

    /**
     * 根据key获取值
     *
     * @param key 全名key
     * @return
     */
    public static String getValue(String key) {
        Map<String, String> map = getYmlByFileName(null);
        if (map == null) {
            return null;
        }
        return map.get(key.trim());
    }

    /**
     * 根据文件名获取yml的文件内容 ,null 默认application.yml
     *
     * @param file 配置文件名
     * @return
     */
    public static Map<String, String> getYmlByFileName(String file) {
        result = new HashMap<>();

        if (file == null) {
            file = bootstrap_file;
        }
        InputStream in = YmlUtil.class.getClassLoader().getResourceAsStream(file);
        Yaml props = new Yaml();
        Object obj = props.loadAs(in, Map.class);
        Map<String, Object> param = (Map<String, Object>) obj;

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            if (val instanceof Map) {
                forEachYaml(key, (Map<String, Object>) val);
            } else {
                result.put(key, val.toString());
            }
        }
        return result;
    }


    /**
     * 遍历yml文件，获取map集合
     *
     * @param keyStr
     * @param obj
     * @return
     */
    public static Map<String, String> forEachYaml(String keyStr, Map<String, Object> obj) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            String strNew = "";
            if (!StringUtils.isEmpty(keyStr)) {
                strNew = keyStr + "." + key;
            } else {
                strNew = key;
            }

            if (val instanceof Map) {
                forEachYaml(strNew, (Map<String, Object>) val);
            } else {
                result.put(strNew, val.toString());
            }
        }
        return result;
    }


    public static void main(String[] args) {
        String value = getValue("spring.profiles.active");
        System.out.println("spring.profiles.active = " + value);

        Map<String, String> ymlByFileName = getYmlByFileName("application-dev.yml");
        System.out.println(ymlByFileName);

    }
}
