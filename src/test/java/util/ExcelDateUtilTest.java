package util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    }
}
