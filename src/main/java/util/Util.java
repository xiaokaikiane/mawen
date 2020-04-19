package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util{
    public static final String DATE_PATTERN="yyyy-MM-dd HH:mm:ss";

    public static String parseSize(long size) {
        String[] danweis={"B","KB","MB","GB","PB","TB"};
        int index=0;
        while(size>1024&&index<danweis.length-1){
            size/=1024;
            index++;
        }
        return size+danweis[index];
    }

    /**
     * 解析日期为中文
     * @param lastmodified
     * @return
     */
    public static String parseDate(Date lastmodified) {
        return new SimpleDateFormat(DATE_PATTERN).format(lastmodified);
    }

    public static void main(String[] args) {

    }
}