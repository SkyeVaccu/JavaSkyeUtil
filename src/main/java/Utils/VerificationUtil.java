package Utils;

import Bean.ImageVerificationCode;
import Log.SkyeLogger;
import SkyeUtilsException.SkyeUtilsExceptionFacoty;
import SkyeUtilsException.SkyeUtilsExceptionType;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * use the jwt draw the verification code image
 */
public class VerificationUtil {
    //日志组件，用于打印对应的错误日志
    private final static Logger logger= SkyeLogger.getLogger();



    @Accessors(chain = true)
    @Setter
    class VerificationBuilder{
        private int width=120;
        private int height=40;
        private Color backgroundColor=Color.WHITE;
        private Color borderColor=Color.WHITE;
        private int randomLineNum=4;
        private int randomStrDigit=4;

        public ImageVerificationCode build() throws Exception {
            if(width<=0 || height <=0)
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.VerificationImageSizeErrorException);
            if(null==backgroundColor||null==borderColor){
                logger.error("背景颜色或边框颜色不能为Null");
                throw new NullPointerException();
            }
            if(randomLineNum<0)
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.VerificationRandomLineNumErrorException);
            if(randomStrDigit<0)
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.VerificationRandomStrNumErrorException);
            return getImageVerifiacationCode();
        }


        /**
         * draw the verificationCode
         * @param graphics the paint pen
         * @return verificationCode
         */
        private String drawVerificationCode(Graphics2D graphics){
            if(null==graphics){
                logger.error("画笔对象不能为空");
                throw new NullPointerException();
            }
            //draw the background
            graphics.setColor(backgroundColor);
            graphics.fillRect(0,0,width,height);
            //draw the border
            graphics.setColor(borderColor);
            graphics.drawRect(1,1,width-2,height-2);
            //draw the random line
            int[] randomLinesColor  = Arrays.stream(new int[3]).map(e -> (int) (Math.random() * 255)).toArray();
            graphics.setColor(new Color(randomLinesColor[0],randomLinesColor[1],randomLinesColor[2],100));
            Stream.of(new int[randomLineNum][4])
                    .map(temp -> (new int[]{(int) Math.random() * width, (int) Math.random() * height,(int) Math.random() * width, (int) Math.random() * height}))
                    .forEach(temp->graphics.drawLine(temp[0],temp[1],temp[2],temp[3]));
            //draw the randomstr
            String randomStr = StringUtil.getRandomStr(randomStrDigit);
            int[] charX=new int[]{5};
            Stream.of(randomStr.toCharArray()).forEach(temp->{
                // set the char degree
                int degree = new Random().nextInt() % 30;
                // 将笔正向角度，以及旋转原点的x轴和y轴
                graphics.rotate(degree * Math.PI / 180, charX[0], 20);
                graphics.setFont(new Font("Arial",Font.PLAIN,18));
                //在指定的位置绘制字符串
                graphics.drawString(new String(temp), charX[0] , 20);
                // 反向角度，将笔转回来
                graphics.rotate(-degree * Math.PI / 180, charX[0], 20);
                charX[0] += ((width-10)/randomStrDigit);
            });
            return randomStr;
        }

        /**
         * get the verificationCode image
         * @return
         */
        private ImageVerificationCode getImageVerifiacationCode() throws Exception {
            try {
                //create the bufferimage in cache
                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                //create the piant pen
                Graphics graphics = bufferedImage.getGraphics();
                //create the return object
                ImageVerificationCode imageVerificationCode = new ImageVerificationCode()
                        .setTrueCode(drawVerificationCode((Graphics2D) graphics));
                //convert the jwt image to a byte[]
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                byte[] bytes = byteArrayOutputStream.toByteArray();
                imageVerificationCode.setImageMetaData(Base64.getEncoder().encodeToString(bytes));
                return imageVerificationCode;
            }catch (Exception e){
                throw SkyeUtilsExceptionFacoty.createException(SkyeUtilsExceptionType.CreateVerificationImageErrorException);
            }
        }
    }

    public VerificationBuilder newBuilder(){
        return new VerificationBuilder();
    }
}
