package lk.ijse.dep.servlet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public class TestClass {

//    public static void main(String[] args) throws ParseException {
////        System.out.println(LocalDate.parse("2019/06/13", DateTimeFormatter.ofPattern("yyyy/MM/dd")));
//        System.out.println(formatDate2("6/13/2019"));
//    }

    public static String formatDate1(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date parse = sdf.parse(date);
        sdf.applyPattern("yyyy-MM-dd");
        return sdf.format(parse);
    }

    public static String formatDate2(String date) throws ParseException {
        LocalDate parse = LocalDate.parse(date,DateTimeFormatter.ofPattern("M/d/y"));
        return parse.format(DateTimeFormatter.ofPattern("y-MM-dd"));
    }

}
