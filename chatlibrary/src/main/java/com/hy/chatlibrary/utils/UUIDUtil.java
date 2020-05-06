package com.hy.chatlibrary.utils;

import java.util.UUID;

/**
 * @author:MtBaby
 * @date:2020/04/02 16:35
 * @desc:
 */
public class UUIDUtil {
    public static String getUUID() {
        String id = null;
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        id = id.toUpperCase();
        //去掉随机ID的短横线
//        id = id.replace("-", "");
//        //将随机ID换成数字
//        int num = id.hashCode();
//        //去绝对值
//        num = num < 0 ? -num : num;
//        id = String.valueOf(num);
        return id;
    }
}
