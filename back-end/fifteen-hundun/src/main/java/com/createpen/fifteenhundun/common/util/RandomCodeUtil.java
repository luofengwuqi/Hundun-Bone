package com.createpen.fifteenhundun.common.util;

import java.util.Random;

public class RandomCodeUtil {
    static String EnglishCode = "ABCDEFJHIJKIMNOPQRSTUVWXYZ";
//    static String NumCode="1234567890";
    public static String makeComplexCode(int length){
        StringBuilder code =new StringBuilder("");
        for(int i = 0 ; i < length ; i++)
        {
            Random r = new Random();
            int NumOrChar=r.nextInt(10);
            //情况一 塞数字
            if(NumOrChar <=4){
                code.append(getRandomSingleInt());
//                code.append();
            }
            //情况二 塞字母
            else{
                code.append(getRandomSingleChar());
            }
        }

        return code.toString();
    }

    public static int getRandomSingleInt(){
        Random r =new Random();
        return r.nextInt(10);
    }
    public static char getRandomSingleChar(){
        Random r = new Random();
        //随机拿到一个字母
        int which = r.nextInt(26);
        char code = EnglishCode.charAt(which);
        //随机大小写
        int lowOrUp = r.nextInt(2);
        if(lowOrUp == 0)
        {
            code = (char)(code + 32);
        }


        return code;
    }
}
