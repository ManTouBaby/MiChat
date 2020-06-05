package com.hy.chatlibrary.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.hy.chatlibrary.widget.chatinput.expression.ExpressionBO;
import com.hy.chatlibrary.widget.chatinput.expression.ExpressionData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:MtBaby
 * @date:2020/04/19 11:09
 * @desc:
 */
public class StringUtil {
    public static String isEmpty(String label) {
        return isEmpty(label, "");
    }

    public static String isEmpty(String label, String defaultStr) {
        return TextUtils.isEmpty(label) ? defaultStr : label;
    }

    public static void matchExpression(TextView textView, String content) {
        Context context = textView.getContext();
        Pattern pattern = Pattern.compile("#\\[(.*?)\\]#", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        try {
            while (matcher.find()) {
                float density = context.getResources().getDisplayMetrics().density;
                int space = (int) (3 * density);
                int start = matcher.start();
                int end = matcher.end();
                String group = matcher.group(1);
                Drawable drawable = context.getResources().getDrawable(getResId(group));
                int size = (int) (26 * density) - space;
                drawable.setBounds(space, space, size , size );
                ImageSpan imageSpan = new ImageSpan(drawable);
//                ImageSpan imageSpan = new ImageSpan(context, BitmapFactory.decodeResource(context.getResources(), getResId(group)));
                spannableStringBuilder.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    public static void insertExpression(EditText editText, ExpressionBO expressionBO) {
        Context context = editText.getContext();
        float density = context.getResources().getDisplayMetrics().density;
        int space = (int) (3 * density);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */

            Drawable drawable = context.getResources().getDrawable(getResId(expressionBO.getExpressionName()));
            int size = (int) (26 * density) - space;
            drawable.setBounds(space, space, size, size);
            ImageSpan imageSpan = new ImageSpan(drawable);
            String tempText = "#[" + expressionBO.getExpressionName() + "]#";
            sb.append(tempText);
            sb.setSpan(imageSpan, sb.length() - tempText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            int iCursorStart = Selection.getSelectionStart((editText.getText()));
            int iCursorEnd = Selection.getSelectionEnd((editText.getText()));
            if (iCursorStart != iCursorEnd) {
                editText.getText().replace(iCursorStart, iCursorEnd, "");
            }
            int iCursor = Selection.getSelectionEnd((editText.getText()));
            editText.getText().insert(iCursor, sb);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static SpannableStringBuilder getStringBuilder(Context context, String label) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append(label);
        stringBuilder.setSpan(new ImageSpan(context, BitmapFactory.decodeResource(context.getResources(), getResId(label))),
                stringBuilder.length() - label.length(),
                stringBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    private static int getResId(@NonNull String label) {
        int resId = -1;
        List<ExpressionBO> expressions = ExpressionData.getExpressions();
        for (ExpressionBO expressionBO : expressions) {
            if (label.equals(expressionBO.getExpressionName())) {
                resId = expressionBO.getExpressionRes();
                break;
            }
        }
        return resId;
    }

    /**
     * 正则表达式匹配两个指定字符串中间的内容
     *
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap, String rgex) {
        //"@(.*?) "
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }
}
