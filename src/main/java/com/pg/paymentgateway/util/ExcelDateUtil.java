package com.pg.paymentgateway.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.SimpleDateFormat;
@Slf4j
public class ExcelDateUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDateUtil.class);
    public Date parseDate(String date, SimpleDateFormat sdf, String record){
        Date sqldate = null;

        try {
            sqldate = new Date(sdf.parse(date).getTime());

        } catch (Exception e) {
            // Handle the parse exception
            logger.error("Unable to parse date -" + date + "for record - " + record);
            e.printStackTrace();
        }
        finally {
            return sqldate;
        }


    }
}
