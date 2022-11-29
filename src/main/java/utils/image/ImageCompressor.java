package utils.image;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;

/**
 * @Description the tool to compress the image @Author Skye @Date 2022/11/25 20:15
 */
@Accessors(chain = true)
@Setter
public class ImageCompressor {
    // 压缩倍率
    private float compressRatio = 0.3f;
    // the old file
    private File file = null;
    // the old file path
    private String oldPath;
    // the new target file path
    private String newPath;

    /** build the builder for completing the image */
    public void compress() {
        // judge the legality of the file or path
        boolean fileState = null != file && ImageUtil.isImage(file.getAbsolutePath());
        boolean oldPathState = ImageUtil.isImage(oldPath);
        boolean newPathState = ImageUtil.isImage(newPath);

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
                compressImage(
                        file.getAbsolutePath(),
                        newPathState ? newPath : file.getAbsolutePath(),
                        this.compressRatio);
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
     * @param oldPath 文件的旧路径
     * @param newPath 文件的新路径
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
