package com.hy.chatlibrary.bean;

import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author:MtBaby
 * @date:2020/04/01 9:28
 * @desc:
 */
public class DateModel {
    public static String labels[] = {
            "人到中年，如在生活的激流中行走，习惯了背过身子哭，转过身子笑。有时候，只想沉默，不是不懂，不是装清高，只是累了，不想说。",
            "成年人的世界里，越发懂得独处，闲暇时，打开音乐，沉浸在一个人的世界里，在高低起伏的音乐声中，抱紧自己。",
            "人活着，有时真的很累，越是外表开朗的人，内心越脆弱，越是表面看起来无所谓的人，越在意的多。",
            "每个人的心里都有一方天地，你走不进来，他走不出去，于是便学会了在自己的世界里，浅笑成歌。",
            "慢慢地学会了，苦而不言，喜而不语，因为你的不容易，别人未必能够读懂，你走的路，只是别人眼中的一处风景。",
            "再坚强的人，也会委屈，也会难过，但是你要相信，每个人头顶上都有一片天，每个人的人生都会有阴晴圆缺。",
            "人到中年，要学会自己去奔跑，每天睁开眼睛就面临着不可推卸的责任，所以再苦再累都不能倒下。",
            "张爱玲说，到了中年的男人时常会感觉孤独，因为他一睁眼全是要依靠他的人，而没有他可以依靠的人。",
            "中年人生，渴望有一双大的翅膀，为亲人撑起一方天空，总想给孩子一片森林，总想成为父母眼中的骄傲，总想成为朋友中的佼佼者，因而要咬紧牙关，不断的充实自己。",
            "人到中年，看不透的人心，放不下的责任，无论深夜如何纠结，无论内心有怎样的疲惫，天亮依然去奋斗， 无论有怎样的迷茫，都不敢停下前行的脚步。"
    };
    public static String images[] = {
            "http://attach.bbs.miui.com/forum/201309/01/151240r9vyme4njopygv49.jpg",
            "https://b-ssl.duitang.com/uploads/item/201805/26/20180526193413_FH8iK.thumb.700_0.jpeg",
            "https://p0.ssl.qhimgs1.com/sdr/400__/t018ec626cb5e4afb46.webp",
            "http://i0.hdslb.com/bfs/article/8e87829cde9559c8407892aa6110f83a4631c6b3.jpg",
            "https://p5.ssl.qhimgs1.com/sdr/400__/t010af94c2dd6608e05.webp",
            "https://p3.ssl.qhimgs1.com/sdr/400__/t01b04e950abe78273d.webp",
            "http://img.netbian.com/file/2017/0301/ddb1e696ffe21b1466d386bcb3b59bea.jpg",
            "http://img1.gtimg.com/gamezone/pics/hv1/120/230/2185/142138395.jpg",
            "https://b-ssl.duitang.com/uploads/item/201706/28/20170628110302_Gtrze.thumb.700_0.jpeg",
            "http://img.zcool.cn/community/01559156820e2b6ac7251bb6211eee.jpg@900w_1l_2o"
    };

    //语音地址
    public static File voices[] = {
            new File("/storage/emulated/0/hy//audio20200414_162522.m4a", 9809),//语音测试一
            new File("/storage/emulated/0/hy//audio20200414_163035.m4a", 6329),//语音测试二
            new File("/storage/emulated/0/hy//audio20200414_163125.m4a,", 4308),
            new File("/storage/emulated/0/hy//audio20200414_163132.m4a", 8074),
            new File("/storage/emulated/0/hy//audio20200414_163141.m4a", 4954),
            new File("/storage/emulated/0/hy//audio20200414_163146.m4a", 8753),
            new File("/storage/emulated/0/hy//audio20200414_163205.m4a", 3939),
            new File("/storage/emulated/0/hy//audio20200414_163212.m4a", 3036),
            new File("/storage/emulated/0/hy//audio20200414_163219.m4a", 2890),
            new File("/storage/emulated/0/hy//audio20200414_163225.m4a", 5071),

    };

    //视屏地址
    public static File videos[] = {
            new File("/storage/emulated/0/DCIM/Camera/20200414_150629.mp4", 8486),
            new File("/storage/emulated/0/DCIM/Camera/20200414_150012.mp4", 7371),
            new File("/storage/emulated/0/DCIM/Camera/20200414_145718.mp4", 37371),
            new File("/storage/emulated/0/DCIM/Camera/20200414_145600.mp4", 5217),
            new File("/storage/emulated/0/DCIM/Camera/20200414_145600.mp4", 5217),
            new File("/storage/emulated/0/DCIM/Camera/20200414_142622.mp4", 8452),
            new File("/storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1586837817823.mp4", 7082),
            new File("/storage/emulated/0/DCIM/Camera/20200413_171657.mp4", 3423),
            new File("/storage/emulated/0/DCIM/Camera/20200413_162549.mp4", 5623),
            new File("/storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1586319365535.mp4", 11099)
    };

    public static LocationBO locationBOS[] = {
            new LocationBO("珠江国际大厦", "越华路112号", 113.267585, 23.129623),
            new LocationBO("广仁路小区", "广仁路与广卫路交叉口北100米", 113.267993, 23.128134),
            new LocationBO("人民公园", "府前路", 113.264286, 23.128676),
            new LocationBO("六荣楼", "解放北路603号", 113.260966, 23.12763),
            new LocationBO("书坊文化广场", "惠福东路与步行街交叉口西南50米", 113.266309, 23.120664),
            new LocationBO("张记瓦罐汤", "纸行路11号首层", 113.254807, 23.12133),
            new LocationBO("潮香里餐厅", "一德中路紫薇街14号", 113.258934, 23.113561),
            new LocationBO("金砖创意中心", "滨江西路100号", 113.253296, 23.105597),
            new LocationBO("趣苑花鸟鱼虫店", "万松路33号", 113.273413, 23.099045),
            new LocationBO("趣苑花鸟鱼虫店", "万松路33号", 113.273413, 23.099045),
    };

    static class File {
        String content;
        long duration;

        public File(String content, long duration) {
            this.content = content;
            this.duration = duration;
        }
    }

    static class LocationBO {
        private String locationAddress;//位置名称
        private String locationRoad;//位置所在路段
        private double latitude;//纬度
        private double longitude;//经

        public LocationBO(String locationAddress, String locationRoad, double longitude, double latitude) {
            this.locationAddress = locationAddress;
            this.locationRoad = locationRoad;
            this.latitude = latitude;
            this.longitude = longitude;
        }


    }


    private static int mCount = 0;

    public static void createChatMessageByTime(String messageGroupId, final onChatMessageCreateListener messageCreateListener) {
        mCount = 0;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mCount += 1;
                ChatMessage chatMessage = createChatMessage(messageGroupId);
                messageCreateListener.onChatMessageCreate(chatMessage);
                if (mCount == 80) timer.cancel();
            }
        }, 0, 3000);


    }

    public static List<ChatMessage> getChatMessages(int count, String messageGroupId) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ChatMessage chatMessage = createChatMessage(messageGroupId);
            chatMessages.add(chatMessage);
        }
        return chatMessages;
    }

    private static ChatMessage createChatMessage(String messageGroupId) {
        int holderType = (int) (Math.random() * 2);
        int itemType = (int) (Math.random() * 6);
        int index = (int) (Math.random() * 10);
        ChatMessage chatMessage = createChatMessage(holderType, itemType);
        chatMessage.setMessageGroupId(messageGroupId);
        switch (itemType) {
            case 0:
                chatMessage.setMessageContent(labels[index]);
                chatMessage.setMessageLocalContent(labels[index]);
                break;
            case 1:
                chatMessage.setMessageContent(voices[index].content);
                chatMessage.setMessageLocalContent(voices[index].content);
                chatMessage.setDuration(voices[index].duration);
                break;
            case 2:
                chatMessage.setMessageContent(videos[index].content);
                chatMessage.setMessageLocalContent(videos[index].content);
                chatMessage.setDuration(videos[index].duration);
                break;
            case 3:
                chatMessage.setMessageContent(images[index]);
                chatMessage.setMessageLocalContent(images[index]);
                break;
            case 4:
                LocationBO locationBO = locationBOS[index];
                chatMessage.setLatitude(locationBO.latitude);
                chatMessage.setLongitude(locationBO.longitude);
                chatMessage.setLocationAddress(locationBO.locationAddress);
                chatMessage.setLocationRoad(locationBO.locationRoad);
                break;
        }

        return chatMessage;
    }

    private static ChatMessage createChatMessage(int holder, int contentType) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(UUIDUtil.getUUID());
        chatMessage.setMessageOwner(holder);
        chatMessage.setItemType(contentType);
        chatMessage.setMessageCT(DateUtil.getSystemTime());
        chatMessage.setMessageCTMillis(DateUtil.getSystemTimeMilli());
        chatMessage.setMessageST(DateUtil.getSystemTime());
        chatMessage.setMessageSTMillis(DateUtil.getSystemTimeMilli());
        chatMessage.setMessageStatus(0);
        return chatMessage;
    }

    public static interface onChatMessageCreateListener {
        void onChatMessageCreate(ChatMessage chatMessage);
    }
}
