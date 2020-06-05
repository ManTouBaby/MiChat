package com.hy.chatlibrary.widget.chatinput.expression;

/**
 * @author:MtBaby
 * @date:2020/06/02 11:28
 * @desc:
 */
public class ExpressionBO {
    private String expressionName;
    private int expressionRes;

    public ExpressionBO(String expressionName, int expressionRes) {
        this.expressionName = expressionName;
        this.expressionRes = expressionRes;
    }

    public String getExpressionName() {
        return expressionName;
    }

    public int getExpressionRes() {
        return expressionRes;
    }
}
