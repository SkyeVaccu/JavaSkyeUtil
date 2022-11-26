package utils.web_socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @Description WebSocket数据包
 * @Author Skye
 * @Date 2022/11/25 22:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WebSocketPackage {
    //该包的id
    private String id;
    //包响应的Id
    private String responseId;
    //包的内容
    private Object content;
    //该包的发送时间
    private Long time;
    //该包所在的回路
    private String loop;
    // 该包在回路中的主题
    private String subject;
    //该数据包的类型，发送或者接收数据包
    private WebSocketPackageType webSocketPackageType;
    //附加信息
    public Map<String, Object> additionalInfos;

    public enum WebSocketPackageType {
        //发送数据包
        SEND,
        //接收数据包
        RECEIVE;
    }
}
