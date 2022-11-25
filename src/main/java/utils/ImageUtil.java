package utils;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;

/**
 * @Description 照片工具
 * @Author Skye
 * @Date 2022/11/25 11:08
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

    /**
     * the tool to compress the image
     */
    @Accessors(chain = true)
    @Setter
    public static class ImageCompressBuilder {
        // 压缩倍率
        private float compressRatio = 0.3f;
        // the old file
        private File file = null;
        // the old file path
        private String oldPath;
        // the new target file path
        private String newPath;

        /**
         * build the builder for completing the image
         */
        public void build() {
            // judge the legality of the file or path
            boolean fileState = null != file && isImage(file.getAbsolutePath());
            boolean oldPathState = isImage(oldPath);
            boolean newPathState = isImage(newPath);

            // if the compressRatio is too small
            if (0.1 > compressRatio) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.CompressRatioIsTooSmallException);
            }
            // there isn't the file
            if (!fileState) {
                if (!oldPathState) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.LackOldFilePathException);
                } else if (!newPathState) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.LackNewFilePathException);
                } else {
                    compressImage(oldPath, newPath, this.compressRatio);
                }
            } else {
                // is valid for compressing the image
                if (!oldPathState) {
                    compressImage(file.getAbsolutePath(), newPathState ? newPath : file.getAbsolutePath(), this.compressRatio);
                } else if (!newPathState) {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.LackNewFilePathException);
                } else {
                    throw SkyeUtilsExceptionFactory.createException(
                            SkyeUtilsExceptionType.FileInfoConflictException);
                }
            }
        }

        /**
         * the true process for compress image
         *
         * @param oldPath       文件的旧路径
         * @param newPath       文件的新路径
         * @param compressRatio 压缩比例
         */
        private void compressImage(String oldPath, String newPath, float compressRatio) {
            try {
                // 先转换成jpg,大小仍然是1倍
                // 允许将文件进行覆盖
                Thumbnails.of(oldPath).scale(1.0).allowOverwrite(true).toFile(newPath);
                // 将转换后的jpg文件进行压缩，质量是传入的倍率
                Thumbnails.of(newPath)
                        .scale(1.0)
                        .outputQuality(compressRatio)
                        .allowOverwrite(true)
                        .toFile(newPath);
                // 如果原文件不是jpg，则删除源文件
                if (!oldPath.endsWith(".jpg")) {
                    boolean deleteResult = new File(oldPath).delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.CompressImageException);
            }
        }
    }
}
