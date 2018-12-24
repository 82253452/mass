package com.f4w.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Pinyin4j {

    HanyuPinyinOutputFormat format = null;

    public static enum Type {
        UPPERCASE,              //全部大写
        LOWERCASE,              //全部小写
        FIRSTUPPER            //首字母大写
    }

    public Pinyin4j() {
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 转换全部大写
     *
     * @param str 字符串
     * @return str为颐和园 ,return获取到的是YHY
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public String toPinYinUppercase(String str) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, "", Type.UPPERCASE);
    }

    /**
     * 转换全部大写
     *
     * @param str   字符串
     * @param spera 转换字母间隔加的字符串,如果不需要为""
     * @return str为颐和园 ,spera为** return获取到的是Y**H**Y
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public String toPinYinUppercase(String str, String spera) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, spera, Type.UPPERCASE);
    }

    /**
     * 转换全部小写
     *
     * @param str 字符串
     * @throws BadHanyuPinyinOutputFormatCombination
     * @return str为颐和园 ,return获取到的是yhy
     */
    public String toPinYinLowercase(String str) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, "", Type.LOWERCASE);
    }

    /**
     * 转换全部小写
     *
     * @param str   字符串
     * @param spera 转换字母间隔加的字符串,如果不需要为""
     * @throws BadHanyuPinyinOutputFormatCombination
     * @return str为颐和园 ,spera为** return获取到的是y**h**y
     */
    public String toPinYinLowercase(String str, String spera) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, spera, Type.LOWERCASE);
    }

    /**
     * 获取拼音首字母(大写)
     *
     * @param str 字符串
     * @return str为颐和园 ,return获取到的是Y
     * @throws BadHanyuPinyinOutputFormatCombination 异常信息
     */
    public String toPinYinUppercaseInitials(String str) throws BadHanyuPinyinOutputFormatCombination {
        String initials = null;
        String py = toPinYinUppercase(str);
        if (py.length() > 1) {
            initials = py.substring(0, 1);
        }
        if (py.length() <= 1) {
            initials = py;
        }
        return initials.trim();
    }

    /**
     * 获取拼音首字母(小写)
     *
     * @param str 字符串
     * @return str为颐和园 ,return获取到的是y
     * @throws BadHanyuPinyinOutputFormatCombination 异常信息
     */
    public String toPinYinLowercaseInitials(String str) throws BadHanyuPinyinOutputFormatCombination {
        String initials = null;
        String py = toPinYinLowercase(str);
        if (py.length() > 1) {
            initials = py.substring(0, 1);
        }
        if (py.length() <= 1) {
            initials = py;
        }
        return initials.trim();
    }

    /**
     * 将str转换成拼音，如果不是汉字或者没有对应的拼音，则不作转换
     *
     * @param str   字符串
     * @param spera 默认,可为""
     * @param type  转换格式
     * @return 按照转换格式转换成字符串
     * @throws BadHanyuPinyinOutputFormatCombination 异常信息
     */
    public String toPinYin(String str, String spera, Type type) throws BadHanyuPinyinOutputFormatCombination {
        if (str == null || str.trim().length() == 0) {
            return "";
        }
        if (type == Type.UPPERCASE) {
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        } else {
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        }
        String py = "";
        String temp = "";
        String[] t;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((int) c <= 128) {
                py += c;
            } else {
                t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (t == null) {
                    py += c;
                } else {
                    temp = t[0];
                    if (type == Type.FIRSTUPPER) {
                        temp = t[0].toUpperCase().charAt(0) + temp.substring(1);
                    }
                    if (temp.length() >= 1) {
                        temp = temp.substring(0, 1);
                    }
                    py += temp + (i == str.length() - 1 ? "" : spera);
                }
            }
        }
        return py.trim();
    }
}
