package utils;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import utils.string.SubstringOperator;

import java.io.File;

/**
 * @Description a tool for operating the file
 * @Author Skye
 * @Date 2022/11/25 11:01
 */
public class FileUtil {

    private static final Logger logger = SkyeLogger.getLogger();

    /**
     * change the file name
     *
     * @param file    the old file
     * @param newName the new file name
     * @return new file
     */
    public static File changeFileName(File file, String newName) {
        if (null == file || StringUtils.isEmpty(newName)) {
            logger.error("文件或新文件名不能为空");
            throw new NullPointerException();
        }
        try {
            String absolutePath = file.getAbsolutePath();
            // get the path of new file
            String newPath = new SubstringOperator(absolutePath)
                    .setStartSign("/")
                    .setStartSignIsStartIndexOf(false)
                    .setEndSign(".")
                    .setEndSignIsStartIndexOf(false)
                    .build(
                            (startIndex, endIndex) ->
                                    StringUtils.substring(absolutePath, 0, startIndex)
                                            + newName
                                            + StringUtils.substring(absolutePath, endIndex, absolutePath.length()));
            File outputFile = new File(newPath);
            //rename the file
            boolean renameResult = file.renameTo(outputFile);
            return outputFile;
        } catch (Exception e) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.ChangeFileNameErrorException);
        }
    }

    /**
     * get the extension name of the file
     *
     * @param fileName the target file name
     * @return the extension name
     */
    public static String getFileExtensionName(String fileName) {
        return new SubstringOperator(fileName)
                .setStartSign(".")
                .setStartSignIsStartIndexOf(false)
                .build();
    }
}
