package com.six.landing.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    private static final String EMPTY_VALUE = "";

    public static boolean isEmpty(Object value) {
        return (value == null) || (value.equals(""));
    }

    public static boolean isEmpty(String[] stringArray) {
        return (stringArray == null) || (stringArray.length == 0);
    }

    public static boolean isEmpty(Object[] objectArray) {
        return (objectArray == null) || (objectArray.length == 0);
    }

    public static String maskNull(String origin) {
        return origin == null ? "" : origin;
    }

    public static String unmaskNull(String origin) {
        return origin == "" ? null : origin;
    }

    public static String trim(String origin) {
        return maskNull(origin).trim();
    }

    public static String append(String origin, String append, int length, boolean isLeft) {
        origin = maskNull(origin);

        if (origin.getBytes().length == length)
            return origin;
        if (origin.getBytes().length > length) {
            return origin.substring(0, length);
        }

        if (isLeft) {
            while (origin.getBytes().length < length) {
                origin = append + origin;
            }
        }
        while (origin.getBytes().length < length) {
            origin = origin + append;
        }

        return origin;
    }

    public static boolean isNumber(String string) {
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] < '0') || (chars[i] > '9')) {
                return false;
            }
        }
        return true;
    }

    public static String replace(String source, String target, String replace) {
        if ((isEmpty(source)) || (isEmpty(target)) || (replace == null) || (source.indexOf(target) < 0)) {
            return source;
        }
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
        while ((e = source.indexOf(target, s)) >= 0) {
            result.append(source.substring(s, e));
            result.append(replace);
            s = e + target.length();
        }
        result.append(source.substring(s));

        return result.toString();
    }

    public static String replaceAllChar(String string, char regex, char replacement) {
        char[] chars = string.toCharArray();
        String ret = "";
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == regex) {
                chars[i] = replacement;
            }
            ret = ret + chars[i];
        }
        return ret;
    }

    public static String setToCondi(Long[] origns) {
        if ((origns == null) || (origns.length == 0)) {
            return "";
        }
        StringBuilder strCondi = new StringBuilder("");
        for (int i = 0; i < origns.length; i++) {
            strCondi.append(origns[i]).append(",");
        }
        return strCondi.delete(strCondi.length() - 1, strCondi.length()).toString();
    }

    public static String setToCondi(int[] origns) {
        if ((origns == null) || (origns.length == 0)) {
            return "''";
        }
        StringBuilder strCondi = new StringBuilder("");
        for (int i = 0; i < origns.length; i++) {
            strCondi.append(",").append(origns[i]);
        }
        return strCondi.deleteCharAt(0).toString();
    }

    public static String setToCondi(Object[] origns) {
        if ((origns == null) || (origns.length == 0)) {
            return "";
        }
        StringBuilder strCondi = new StringBuilder("'");
        for (int i = 0; i < origns.length; i++) {
            strCondi.append(origns[i]).append("','");
        }

        return strCondi.delete(strCondi.length() - 2, strCondi.length()).toString();
    }

    public static String[] split(String s) {
        if (isEmpty(s))
            return new String[0];
        if (s.indexOf(",") > -1) {
            return split(s, ",");
        }
        String[] i = new String[1];
        i[0] = s;
        return i;
    }

    public static String[] split(String s, String spliter) {
        if ((s == null) || (s.indexOf(spliter) == -1)) {
            return new String[0];
        }
        String[] ary = s.split(spliter);
        String[] result = new String[ary.length];
        try {
            for (int i = 0; i < ary.length; i++)
                result[i] = ary[i];
        } catch (NumberFormatException ex) {
        }
        return result;
    }

    public static String join(String[] s, String spliter) {
        if (isEmpty(s))
            return "";
        if (s.length == 1)
            return s[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            sb.append(s[i]).append(spliter);
        }
        sb.deleteCharAt(sb.lastIndexOf(spliter));
        return sb.toString();
    }

    public static String join(Object[] o, String spliter) {
        if (isEmpty(o))
            return "";
        if (o.length == 1)
            return String.valueOf(o[0]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o.length; i++) {
            sb.append(o[i]).append(spliter);
        }
        sb.deleteCharAt(sb.lastIndexOf(spliter));
        return sb.toString();
    }

    public static String join(int[] arr) {
        if ((arr == null) || (arr.length == 0))
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int len = arr.length; i < len; i++) {
            sb.append(",").append(arr[i]);
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    public static String getHexString(String sourceString) {
        if (isEmpty(sourceString)) {
            return "";
        }
        return encodeHex(sourceString.getBytes());
    }

    public static final String encodeHex(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xFF) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(bytes[i] & 0xFF, 16));
        }
        return buf.toString();
    }

    public static final byte[] decodeHex(String hex) {
        char[] chars = hex.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += 2) {
            int newByte = 0;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[(i + 1)]);
            bytes[byteCount] = (byte) newByte;
            byteCount++;
        }
        return bytes;
    }

    private static final byte hexCharToByte(char ch) {
        switch (ch) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
                return 10;
            case 'b':
                return 11;
            case 'c':
                return 12;
            case 'd':
                return 13;
            case 'e':
                return 14;
            case 'f':
                return 15;
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
        }

        return 0;
    }

    public static String fillEnd(String str, int length, String c) {
        return fill(str, length, c, true);
    }

    public static String fillStart(String str, int length, String c) {
        return fill(str, length, c, false);
    }

    private static String fill(String str, int length, String c, boolean tail) {
        str = str == null ? "" : str;
        int strLength = str.getBytes().length;
        StringBuffer result = new StringBuffer();
        while (strLength++ < length) {
            result.append(c);
        }
        if (tail) {
            return str + result.toString();
        }
        return str;
    }

    public static String repeat(String string, int times) {
        StringBuffer buf = new StringBuffer(string.length() * times);
        for (int i = 0; i < times; i++)
            buf.append(string);
        return buf.toString();
    }

    public static String escapeHtml(String str) {
        if (str != null) {
            str = str.replaceAll("'", "&#039;");
            str = str.replaceAll("\"", "&quot;");
            return str;
        }
        return null;
    }

    public static String stringMD5(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = input.getBytes();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }
    private static String md5(long time,String token){
        return StringUtil.stringMD5(StringUtil.stringMD5(time+"-"+token));
    }

}
