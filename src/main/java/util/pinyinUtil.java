package util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class pinyinUtil {
    /**
     * 中文字符格式
     */
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";
    /**
     * 汉语拼音格式化类
     */
    private static final HanyuPinyinOutputFormat FORMAT=new HanyuPinyinOutputFormat();
    static {
        //设置拼音小写
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //设置不带音调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //设置带V字符,比如lV(绿)
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 字符串是否包含中文
     * @param name
     * @return
     */
    public static boolean containsChinese(String name){
        return name.matches(".*"+CHINESE_PATTERN+".*");
    }
    /**
     * 通过文件名获取一个全拼+拼音首字母
     * @param name 文件名
     * @return 拼音  字符串
     */
    public static String[] get(String name) {
        String[] result = new String[2];
        StringBuilder sb = new StringBuilder();//全拼
        StringBuilder pinyinFirst = new StringBuilder();//首拼
        for (char c : name.toCharArray()) {
            try {
                String[] pinyins =
                        PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                if (pinyins == null || pinyins.length == 0) {
                    sb.append(c);
                    pinyinFirst.append(c);
                } else {
                    //全拼: 和->he
                    sb.append(pinyins[0]);
                    //首拼:  和->h
                    pinyinFirst.append(pinyins[0].charAt(0));
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                sb.append(c);
                pinyinFirst.append(c);
            }
        }
        result[0] = sb.toString();
        result[1]=pinyinFirst.toString();
        return result;
    }
    //多音字

    /**
     *
     * @param name 文件名
     * @param fullSpell true表示全拼  false表示首拼
     * @return  包含多音字的字符串组合  (二维数组)
     */
    public static String[][] get(String name,boolean fullSpell){
        char[] chars=name.toCharArray();
        String[][] result=new String[chars.length][];
        for(int i=0;i<chars.length;i++){
            try {
                String[] pinyins=PinyinHelper
                        .toHanyuPinyinStringArray(chars[i],FORMAT);
                if(pinyins==null||pinyins.length==0){
                    result[i]=new String[]{String.valueOf(chars[i])};
                }else {
                    result[i]=unique(pinyins,fullSpell);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                result[i]=new String[]{String.valueOf(chars[i])};
            }
        }
        return result;
    }

    /**
     * 每个中文字符返回拼音是字符串数组,每两个字符传输组合并为一个字符串数组
     * 之后以此类推
     * @param pinyinarray
     */
    public static String[] compose(String[][] pinyinarray){
        if(pinyinarray==null||pinyinarray.length==0) {
            return null;
        }else if(pinyinarray.length==1){
            return pinyinarray[0];
        }else{
            for(int i=0;i<pinyinarray.length;i++){
                pinyinarray[0]=compose(pinyinarray[0],pinyinarray[i]);
            }
            return pinyinarray[0];
        }
    }

    /**
     * 合并两个拼音数组为一个
     * @param pinyins1
     * @param pinyins2
     * @return
     */
    public static String[] compose(String[] pinyins1,String[] pinyins2){
        String[] result=new String[pinyins1.length*pinyins2.length];
        for(int i=0;i<pinyins1.length;i++){
            for(int j=0;j<pinyins2.length;j++){
                result[i*pinyins2.length+j]=pinyins1[i]+pinyins2[j];
            }
        }
        return result;
    }

    /**
     * 字符串去重操作
     * @param array
     * @param fullSpell
     * @return
     */
    public static String[] unique(String[] array,boolean fullSpell){
        Set<String> set=new HashSet<>();
        for(String s:array){
            if(fullSpell){
                set.add(s);
            }else{
                set.add(String.valueOf(s.charAt(0)));
            }
        }
        return set.toArray(new String[set.size()]);
    }
    public static void main(String[] args) {
//        System.out.println(Arrays.toString(get("中华人民共和国")));
//        System.out.println(Arrays.toString(get("中华1人民b共和国")));
//        System.out.println(Arrays.toString(
//                compose(get("中华人民共和国",true))));
//        System.out.println(Arrays.toString(
//                compose(get("中华人民共和国",false))));
    }
}
