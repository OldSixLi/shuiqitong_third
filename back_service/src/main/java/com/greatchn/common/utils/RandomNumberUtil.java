package com.greatchn.common.utils;

import java.util.Random;

public class RandomNumberUtil {
    private RandomNumberUtil() {
    }

    /**
     * <p>生成固定长度随机数</p>
     *
     * @param length 长队
     * @return 返回随机数字符串
     */
    public static String getRandom(int length) {
        StringBuilder str = new StringBuilder();//定义变长字符串
        Random random = new Random();
        //随机生成数字，并添加到字符串
        for (int i = 0; i < length; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
