package utils.datetim;

import org.junit.Test;
import utils.datetime.DateTimeUtil;

import java.util.Date;

public class DateTimeUtilTest {

    @Test
    public void getDateTest() {
        String dateStr = DateTimeUtil.getDate();
        System.out.println(dateStr);
    }

    @Test
    public void getTimeTest() {
        String dateStr = DateTimeUtil.getTime();
        System.out.println(dateStr);
    }

    @Test
    public void dateTimeNowTest() {
        String dateStr = DateTimeUtil.dateTimeNow(DateTimeUtil.YYYYMMDDHHMMSS);
        System.out.println(dateStr);
    }

    @Test
    public void dateTimeTest() {
        String dateStr = DateTimeUtil.dateTime(new Date());
        System.out.println(dateStr);
    }

    @Test
    public void parseDateToStrTest() {
        String dateStr = DateTimeUtil.parseDateToStr(DateTimeUtil.YYYY_MM_DD_HH_MM_SS, new Date());
        System.out.println(dateStr);
    }

    @Test
    public void dateTimeTest_01() {
        Date date = DateTimeUtil.dateTime(DateTimeUtil.YYYY_MM_DD, "2022-12-26 11:21:06");
        System.out.println(date);
    }


    @Test
    public void convertDateToStringTest() {
        Date date = DateTimeUtil.convertStringToDate("2018-07-16 13:25:23");
        String s = DateTimeUtil.convertDateToString(date);
        System.out.println(s);
    }

    @Test
    public void getServerStartDateTest() {
        Date serverStartDate = DateTimeUtil.getServerStartDate();
        System.out.println(DateTimeUtil.convertDateToString(serverStartDate));
    }

    @Test
    public void getDatePoorTest() {
        String datePoor = DateTimeUtil.getDatePoor(
                DateTimeUtil.dateTime(DateTimeUtil.YYYY_MM_DD_HH_MM_SS, "2018-07-16 13:25:23"),
                DateTimeUtil.dateTime(DateTimeUtil.YYYY_MM_DD_HH_MM_SS, "2018-07-16 12:25:23"));
        System.out.println(datePoor);
    }
}