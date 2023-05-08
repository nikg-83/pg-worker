package com.pg.paymentgateway.util;

import com.pg.paymentgateway.controller.RblBankController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
@Slf4j
public class ExcelDateUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDateUtil.class);
    public static Date parseDate(String date, SimpleDateFormat sdf, String bankname){
        Date sqldate = null;

        try {
            sqldate = new Date(sdf.parse(date).getTime());

        } catch (ParseException e) {
            // Handle the parse exception
            logger.error("Unable to parse date -" + date + "for bank -" + bankname);
        }
        return sqldate;

    }
}
