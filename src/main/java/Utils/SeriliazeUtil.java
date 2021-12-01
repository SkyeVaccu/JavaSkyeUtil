package Utils;

import SkyeUtilsException.SkyeUtilsExceptionType;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * 序列化工具类，专门用于序列化以及反序列化
 */
public class SeriliazeUtil {

    //null则不进行序列化
    private static ObjectMapper objectMapper=new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * 将json字符串转换成Bean对象
     * @param jsonStr json字符串
     * @param <T> 需要转换成的类型
     * @return 返回转换后的对象
     * @throws  Exception 创建对象异常
     */
    public static <T> T  convertJsonToBeanByGenericity(String jsonStr) throws Exception{
        try{
            return (T)objectMapper.readValue(jsonStr, new TypeReference<T>() {});
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanException);
        }
    }


    /**
     * 将json字符串转换成BeanList
     * @param <T> 需要转换的对象
     * @param jsonStr json字符串
     * @return 返回包含指定对象的列表
     * @throws Exception 创建对象列表异常
     */
    public static <T> List<T> convertJsonToBeanListByGenericity(String jsonStr)throws Exception{
        try{
            return (List<T>)objectMapper.readValue(jsonStr, new TypeReference<List<T>>(){});
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanListException);
        }
    }


    /**
     * 将map转换成Bean
     * @param map map对象
     * @param <T> 需要转换的对象
     * @return
     * @throws Exception
     */
    public static <T> T convertMapToBeanByGenericity(Map<String,Object> map) throws Exception{
        try{
            return (T)objectMapper.convertValue(map, new TypeReference<T>(){});
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanException);
        }
    }

    /**
     * 将list<Map>转换成List<Bean>
     * @param map 装有Bean属性的对象
     * @param <T> 需要返回的对象
     * @return 返回的用户列表
     * @throws Exception 创建对象序列异常
     */
    public static <T> List<T> convertMapToBeanListByGenericity(List<Map<String,Object>> map) throws Exception{
        try {
            return (List<T>)objectMapper.convertValue(map, new TypeReference<List<T>>() {});
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanListException);
        }
    }

    /**
     * 将json字符串转换成BeanList
     * @param jsonStr json字符串
     * @param beanClass 对象的class对象
     * @return 返回包含指定对象的列表
     * @throws Exception 创建对象列表异常
     */
    public static List<Object> convertJsonToBeanListByClass(String jsonStr,Class beanClass)throws Exception{
        try{
            return (List<Object>)objectMapper.readValue(jsonStr, beanClass);
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanListException);
        }
    }

    /**
     * 将map转换成Bean
     * @param map map对象
     * @param beanClass 类的对象
     * @return Object应用的对象
     * @throws Exception
     */
    public static Object convertMapToBeanByClass(Map<String,Object> map,Class beanClass) throws Exception{
        try{
            return (Object)objectMapper.convertValue(map, beanClass);
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanException);
        }
    }

    /**
     * 将list<Map>转换成List<Bean>
     * @param map 装有Bean属性的对象
     * @return 返回的用户列表
     * @throws Exception 创建对象序列异常
     */
    public static List<Object> convertMapToBeanListByClass(List<Map<String,Object>> map,Class beanClass) throws Exception{
        try {
            return (List<Object>)objectMapper.convertValue(map, beanClass);
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanListException);
        }
    }

    /**
     * 将json字符串转换成Bean对象
     * @param jsonStr json字符串
     * @param beanClass 对象的class对象
     * @return 返回转换后的对象
     * @throws  Exception 创建对象异常
     */
    public static Object convertJsonToBeanByClass(String jsonStr,Class beanClass) throws Exception{
        try{
            return (Object)objectMapper.readValue(jsonStr, beanClass);
        }catch (Exception e){
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateBeanException);
        }
    }



    /**
     * 将对应的object转换成json对象
     * @param object 对应的object对象
     * @return 返回对应的json字符串
     * @throws Exception 获得对象对应json失败
     */
    public static String convertObjectToJson(Object object) throws Exception {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.GetObjectJsonException);
        }
    }
}
