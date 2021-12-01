package Utils;

import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import SkyeUtilsException.SkyeUtilsExceptionType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;

/**
 * a tool for operating the file
 */
public class FileUtil {

    private final static Logger logger= SkyeLogger.getLogger();

    /**
     * change the file name
     * @param file the old file
     * @param newName the new file name
     * @return new file
     * @throws Exception
     */
    public static File changeFileName(File file,String newName) throws Exception{
        if(null==file||StringUtils.isEmpty(newName)){
            logger.error("文件或新文件名不能为空");
            throw new NullPointerException();
        }
        try {
            String absolutePath = file.getAbsolutePath();
            String newPath = new StringUtil().newBuilder(absolutePath)
                    .setStartSign("/")
                    .setStartSignIsStartIndexOf(false)
                    .setEndSign(".")
                    .setEndSignIsStartIndexOf(false)
                    .build((startIndex,endIndex)->{
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(StringUtils.substring(absolutePath,0,startIndex));
                        stringBuffer.append(newName);
                        stringBuffer.append(StringUtils.substring(absolutePath,endIndex,absolutePath.length()));
                        return stringBuffer.toString();
                    });
            File outputFile = new File(newPath);
            file.renameTo(outputFile);
            return outputFile;
        }catch (Exception e) {
            throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.ChangeFileNameErrorException);
        }
    }
}
