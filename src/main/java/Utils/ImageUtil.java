package Utils;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import SkyeUtilsException.SkyeUtilsExceptionType;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;

/**
 * 照片相关的工具
 */
public class ImageUtil {

    //日志组件，用于打印对应的错误日志
    private final static Logger logger= SkyeLogger.getLogger();

    /**
     * 判断该路径或者文件名是否为照片
     * @param path 路径或者文件名
     * @return 是否为照片
     */
    public static boolean isImage(String path){
        if(StringUtils.isEmpty(path))
            return false;
        String[] imageSuffixes={"jpg","png","jpeg","gif"};
        boolean isImage=false;
        int imageSuffixesLength=imageSuffixes.length;
        for(int i=0;i<imageSuffixesLength;i++){
            if(path.endsWith("."+imageSuffixes[i])){
                isImage=true;
                break;
            }
        }
        return isImage;
    }

    /**
     * 将对应的图片路径转换为jpg结尾的路径
     * @param path 原来的路径
     * @return 转换后的路径
     */
    public static String convertImagePathToJpgSuffix(String path) throws Exception{
        if(StringUtils.isEmpty(path)) {
            logger.error("路径不能为空");
            throw new NullPointerException();
        }
        StringBuffer buffer = new StringBuffer(path);
        buffer.substring(0,path.lastIndexOf("."));
        buffer.append(".jpg");
        return buffer.toString();
    }

    /**
     * the tool to compress the image
     */
    @Accessors(chain = true)
    @Setter
    class ImageCompressBuilder{
        //压缩倍率
        private float compressRatio=0.3f;
        //the old file
        private File file=null;
        // the old file path
        private String oldPath;
        // the new target file path
        private String newPath;

        /**
         * build the builder for completing the image
         * @throws Exception
         */
        public void build() throws Exception {
            //judge the legality of the file or path
            boolean fileState=null==file?false:isImage(file.getAbsolutePath());
            boolean oldPathState=isImage(oldPath);
            boolean newPathState=isImage(newPath);

            //if the compressRatio is too small
            if(0.1>compressRatio)
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CompressRatioIsTooSmallException);
            //there isn't the file
            if(!fileState) {
                if(!oldPathState)
                    throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.LackOldFilePathException);
                else if(!newPathState)
                    throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.LackNewFilePathException);
                else
                    compressImage(oldPath,newPath,this.compressRatio);
            }
            else{
                if(!oldPathState)
                    //is vaild for compressing the image
                    compressImage(file.getAbsolutePath(),
                            newPathState?newPath:file.getAbsolutePath(),
                            this.compressRatio);
                else if(!newPathState)
                    throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.LackNewFilePathException);
                else
                    throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.FileInfoConflictException);
            }
        }

        /**
         * the true process for compress image
         * @param oldPath
         * @param newPath
         * @param compressRatio
         * @throws Exception
         */
        private void compressImage(String oldPath, String newPath,float compressRatio) throws Exception{
            try {
                //先转换成jpg,大小仍然是1倍
                //允许将文件进行覆盖
                Thumbnails.of(oldPath).scale(1.0).allowOverwrite(true).toFile(newPath);
                //将转换后的jpg文件进行压缩，质量是传入的倍率
                Thumbnails.of(newPath).scale(1.0).outputQuality(compressRatio).allowOverwrite(true).toFile(newPath);
                //如果原文件不是jpg，则删除源文件
                if (!oldPath.endsWith(".jpg"))
                    new File(oldPath).delete();
            }catch (Exception e){
                e.printStackTrace();
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CompressImageException);
            }
        }
    }

    public ImageCompressBuilder newBuilder(){
        return new ImageCompressBuilder();
    }

}
