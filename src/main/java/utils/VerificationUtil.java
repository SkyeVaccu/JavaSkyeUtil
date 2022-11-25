package utils;

import bean.ImageVerificationCode;
import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import log.SkyeLogger;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @Description use the jwt draw the verification code image
 * @Author Skye
 * @Date 2022/11/25 11:18
 */
public class VerificationUtil {
    /**
     * 日志组件，用于打印对应的错误日
     */
    private static final Logger logger = SkyeLogger.getLogger();

    @Accessors(chain = true)
    @Setter
    public static class VerificationBuilder {
        //图片的宽度
        private int width = 120;
        //图片的高度
        private int height = 40;
        //图片的背景色
        private Color backgroundColor = Color.WHITE;
        //图片的字体颜色
        private Color borderColor = Color.WHITE;
        //图片中随机线的数量
        private int randomLineNum = 4;
        //验证码的位数
        private int randomStrDigit = 4;

        public ImageVerificationCode build() {
            if (width <= 0 || height <= 0) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.VerificationImageSizeErrorException);
            }
            if (null == backgroundColor || null == borderColor) {
                logger.error("背景颜色或边框颜色不能为Null");
                throw new NullPointerException();
            }
            if (randomLineNum < 0) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.VerificationRandomLineNumErrorException);
            }
            if (randomStrDigit < 0) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.VerificationRandomStrNumErrorException);
            }
            return getImageVerificationCode();
        }

        /**
         * draw the verificationCode
         *
         * @param graphics the paint pen
         * @return verificationCode
         */
        private String drawVerificationCode(Graphics2D graphics) {
            if (null == graphics) {
                logger.error("画笔对象不能为空");
                throw new NullPointerException();
            }
            // draw the background
            graphics.setColor(backgroundColor);
            graphics.fillRect(0, 0, width, height);
            // draw the border
            graphics.setColor(borderColor);
            graphics.drawRect(1, 1, width - 2, height - 2);
            // draw the random line
            int[] randomLinesColor = Arrays.stream(new int[3]).map(e -> new Random().nextInt() * 255).toArray();
            graphics.setColor(
                    new Color(randomLinesColor[0], randomLinesColor[1], randomLinesColor[2], 100));
            Stream.of(new int[randomLineNum][4]).map(temp ->
                    new int[]{
                            (int) (Math.random() * width),
                            (int) (Math.random() * height),
                            (int) (Math.random() * width),
                            (int) (Math.random() * height)
                    })
                    .forEach(temp -> graphics.drawLine(temp[0], temp[1], temp[2], temp[3]));
            // draw the random str
            String randomStr = StringUtil.getRandomStr(randomStrDigit);
            int[] charX = new int[]{5};
            Stream.of(randomStr.toCharArray())
                    .forEach(
                            temp -> {
                                // set the char degree
                                int degree = new Random().nextInt() % 30;
                                // 将笔正向角度，以及旋转原点的x轴和y轴
                                graphics.rotate(degree * Math.PI / 180, charX[0], 20);
                                graphics.setFont(new Font("Arial", Font.PLAIN, 18));
                                // 在指定的位置绘制字符串
                                graphics.drawString(new String(temp), charX[0], 20);
                                // 反向角度，将笔转回来
                                graphics.rotate(-degree * Math.PI / 180, charX[0], 20);
                                charX[0] += ((width - 10) / randomStrDigit);
                            });
            return randomStr;
        }

        /**
         * get the verificationCode image
         *
         * @return 验证码
         */
        private ImageVerificationCode getImageVerificationCode() {
            try {
                // create the buffer image in cache
                BufferedImage bufferedImage =
                        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                // create the paint pen
                Graphics graphics = bufferedImage.getGraphics();
                // create the return object
                ImageVerificationCode imageVerificationCode =
                        new ImageVerificationCode()
                                .setTrueCode(drawVerificationCode((Graphics2D) graphics));
                // convert the jwt image to a byte[]
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                byte[] bytes = byteArrayOutputStream.toByteArray();
                imageVerificationCode.setImageMetaData(Base64.getEncoder().encodeToString(bytes));
                return imageVerificationCode;
            } catch (Exception e) {
                throw SkyeUtilsExceptionFactory.createException(
                        SkyeUtilsExceptionType.CreateVerificationImageErrorException);
            }
        }
    }
}
