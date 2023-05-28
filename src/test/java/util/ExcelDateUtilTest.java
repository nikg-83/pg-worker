package util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ExcelDateUtilTest {
    public static void main (String args[]) {
        Date sqldate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            sqldate = new Date(sdf.parse("06/02/2023").getTime());

        } catch (ParseException e) {
            // Handle the parse exception

        }
        System.out.println(sqldate.toString());

        ZoneId dubai = ZoneId.of("Asia/Dubai");
        ZonedDateTime uaeDatetime = ZonedDateTime.now(dubai);

        System.out.println("Dubai datetime is " + uaeDatetime);
        System.out.println("Dubai Instant datetime is " + uaeDatetime.toInstant());
        //createDateTime = ZonedDateTime.now(ZoneId.of("Your_Time_Zone_ID"))

        Instant instant = Instant.now();

        System.out.println("Instant now is " + Instant.now());


    }
}
