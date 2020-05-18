package com.hy.chatlibrary.utils;

import android.annotation.SuppressLint;
import android.os.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author:MtBaby
 * @date:2020/03/31 9:21
 * @desc:
 */
public class DateUtil {

    /**
     * 网址访问
     *
     * @param url 网址
     * @return urlDate 对象网址时间
     */
    public static void getNetTimeByURL(String pattern, String url, Handler handler, OnNetDateStringListener dateStringListener) {
        getNetDate(url, handler, date -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(pattern);  //设置日期格式
            dateStringListener.netDateString(df.format(date));
        });

    }

    public static void getNetTimeMilliByURL(String url, Handler handler, OnNetDateMilliListener netMilliListener) {
        getNetDate(url, handler, date -> netMilliListener.netMilli(date.getTime()));
    }

    public static void getNetTimeByURL(String url, Handler handler, OnNetDateStringListener dateStringListener) {
        getNetDate(url, handler, date -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
            dateStringListener.netDateString(df.format(date));
        });
    }

    public static void getNetDate(String url, OnNetDateListener onNetDateListener) {
        new Thread(() -> {
            Date date;
            try {
                URL url1 = new URL(url);
                URLConnection conn = url1.openConnection();  //生成连接对象
                conn.connect();  //连接对象网页
                date = new Date(conn.getDate());  //获取对象网址时间
                Date finalDate = date;
                onNetDateListener.netMilli(finalDate);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void getNetDate(String url, Handler handler, OnNetDateListener onNetDateListener) {
        new Thread(() -> {
            Date date;
            HttpURLConnection conn = null;
            try {
                URL url1 = new URL(url);
                conn = (HttpURLConnection) url1.openConnection();  //生成连接对象
                conn.connect();  //连接对象网页
                date = new Date(conn.getDate());  //获取对象网址时间
                Date finalDate = date;
                handler.post(() -> onNetDateListener.netMilli(finalDate));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                    //主动关闭inputStream
                    //这里不需要进行判空操作
                        conn.getInputStream().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.disconnect();
                }
            }
        }).start();
    }

    public static String getSystemTime(String pattern) {
        //date类系统时间获取
        Date day = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(pattern);  //设置日期格式
        return df.format(day);
    }

    public static long getSystemTimeMilli() {
        return new Date().getTime();
    }

    public static String getSystemTime() {
        return getSystemTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getStringTimeByMilli(long milli) {
        return getStringTimeByMilli(milli, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getDateByMilli(long milli) {
        Date date = new Date();
        date.setTime(milli);
        return date;
    }

    public static String getStringTimeByMilli(long milli, String pattern) {
        Date date = new Date();
        date.setTime(milli);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(pattern);  //设置日期格式
        return df.format(date);
    }

    //毫秒转秒
    public static String long2String(long time) {
        //毫秒转秒
        int sec = (int) time / 1000;
        int min = sec / 60;    //分钟
        sec = sec % 60;        //秒
        if (min < 10) {    //分钟补0
            if (sec < 10) {    //秒补0
                return "0" + min + ":0" + sec;
            } else {
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) {    //秒补0
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }

    public static interface OnNetDateListener {
        void netMilli(Date date);
    }

    public static interface OnNetDateStringListener {
        void netDateString(String nerDateStr);
    }

    public static interface OnNetDateMilliListener {
        void netMilli(long netMilli);
    }
}
