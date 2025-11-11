package com.twotwo.util;

import com.twotwo.model.*;

public class ChineseNameUtil {
    // 将英文角色名映射为中文
    public static String getChineseName(Role.RoleType roleType) {
        switch (roleType) {
            case LADY:
                return "大小姐";
            case GUARD:
                return "护";
            case WITCH:
                return "魔女";
            case DETECTIVE:
                return "侦探";
            case LITTLEYUAN:
                return "小原原";
            case SHINEBLUE:
                return "蓝金";
            case SNOWWHITE:
                return "白雪";
            case HAMSTER:
                return "仓鼠";
            case DOLLMAKER:
                return "人偶师";
            case COOKGIRL:
                return "厨娘";
            case FAIRY:
                return "妖精";
            default:
                return "未知角色";
        }
    }

    // 将英文地点名映射为中文
    public static String getChineseLocation(Location.LocationType locationType) {
        switch (locationType) {
            case DRAGON_CAVE:
                return "龙穴";
            case KITCHEN:
                return "厨房";
            case STORE:
                return "商店";
            case LIBRARY:
                return "图书馆";
            default:
                return "未知地点";
        }
    }
}
