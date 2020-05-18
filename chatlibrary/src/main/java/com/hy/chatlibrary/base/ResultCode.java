package com.hy.chatlibrary.base;

/**
 * @author:MtBaby
 * @date:2020/04/03 10:58
 * @desc:
 */
public interface ResultCode {
    int REQUEST_SELECT_IMAGES_CODE = 0x001;//发送相册
    int REQUEST_TAKE_PHOTO_CODE = 0x002;//发送拍照
    int REQUEST_TAKE_VIDEO_CODE = 0x003;//发送拍摄
    int REQUEST_TAKE_INSTRUCT_CODE = 0x004;//发送指令
    int REQUEST_TAKE_LOCAL_CODE = 0x005;//发送位置
    int REQUEST_TAKE_PHOTO_VIDEO = 0x006;//指令编辑，获取图片、视屏
    int REQUEST_TAKE_INSTRUCT_MODEL = 0x007;//指令编辑，获取模板
    int REQUEST_TAKE_INSTRUCT_NEW_MODEL = 0x008;//指令编辑，获取模板
    int REQUEST_TAKE_INSTRUCT_MEMBERS = 0x009;//指令编辑，设置接收人员
    int REQUEST_GROUP_DETAIL = 0x010;//群组设置
}
