package utils.image;

import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * @Description 照片工具 @Author Skye @Date 2022/11/25 11:08
 */
public class ImageUtil {

    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * 判断该路径或者文件名是否为照片
     *
     * @param path 路径或者文件名
     * @return 是否为照片
     */
    public static boolean isImage(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        String[] imageSuffixes = {"jpg", "png", "jpeg", "gif"};
        boolean isImage = false;
        for (String imageSuffix : imageSuffixes) {
            if (path.endsWith("." + imageSuffix)) {
                isImage = true;
                break;
            }
        }
        return isImage;
    }

    /**
     * 将对应的图片路径转换为jpg结尾的路径
     *
     * @param path 原来的路径
     * @return 转换后的路径
     */
    public static String convertImagePathToJpgSuffix(String path) {
        if (StringUtils.isEmpty(path)) {
            logger.error("路径不能为空");
            throw new NullPointerException();
        }
        return path.substring(0, path.lastIndexOf(".")) + ".jpg";
    }
}
