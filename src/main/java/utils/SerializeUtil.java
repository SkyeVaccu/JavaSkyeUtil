package utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;

import java.util.List;
import java.util.Map;

/**
 * @Description 序列化工具类，专门用于序列化以及反序列化
 * @Author Skye
 * @Date 2022/11/25 11:12
 */
public class SerializeUtil {

    // Jackson配置Mapper
    // 不存在时为null
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * 将json字符串转换成BeanList
     *
     * @param jsonStr           json字符串
     * @param beanTypeReference 复杂类对象参考
     * @return 返回包含指定对象的列表
     */
    public static <T> T convertJsonToBeanListByClass(String jsonStr, TypeReference<T> beanTypeReference) {
        try {
            return OBJECT_MAPPER.convertValue(jsonStr, beanTypeReference);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.CreateBeanListException);
        }
    }

    /**
     * 将map转换成Bean
     *
     * @param map               map对象
     * @param beanTypeReference 复杂类对象参考
     * @return Object应用的对象
     */
    public static <T> T convertMapToBeanByClass(Map<String, Object> map, TypeReference<T> beanTypeReference) {
        try {
            return OBJECT_MAPPER.convertValue(map, beanTypeReference);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.CreateBeanException);
        }
    }

    /**
     * 将list<Map>转换成List<Bean>
     *
     * @param map               装有Bean属性的对象
     * @param beanTypeReference 复杂类对象参考
     * @return 返回的用户列表
     */
    public static <T> List<T> convertMapListToBeanListByClass(List<Map<String, Object>> map, TypeReference<List<T>> beanTypeReference) {
        try {
            return OBJECT_MAPPER.convertValue(map, beanTypeReference);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.CreateBeanListException);
        }
    }

    /**
     * 将json字符串转换成Bean对象
     *
     * @param jsonStr   json字符串
     * @param beanClass 对象的class对象
     * @return 返回转换后的对象
     */
    public static <T> T convertJsonToBeanByClass(String jsonStr, Class<T> beanClass) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, beanClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.CreateBeanException);
        }
    }

    /**
     * 将对应的object转换成json对象
     *
     * @param object 对应的object对象
     * @return 返回对应的json字符串
     */
    public static String convertObjectToJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.GetObjectJsonException);
        }
    }
}
