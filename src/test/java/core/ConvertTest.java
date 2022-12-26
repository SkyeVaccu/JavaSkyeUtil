package core;

import org.junit.Test;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2022-12-26
 */
public class ConvertTest {

    @Test
    public void toIntTest(){
        Integer integer = Convert.toInt(123, 0);

        System.out.print(integer);
    }

    @Test
    public void toStrTest(){
        String str = Convert.toStr(123, "");

        System.out.print(str);
    }
}
