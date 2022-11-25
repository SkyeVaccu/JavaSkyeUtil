package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description 照片验证码对象
 * @Author Skye
 * @Date 2022/11/25 10:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ImageVerificationCode {
    // 正确的照片验证码
    private String trueCode;
    // 照片的元数据
    private String imageMetaData;
}
