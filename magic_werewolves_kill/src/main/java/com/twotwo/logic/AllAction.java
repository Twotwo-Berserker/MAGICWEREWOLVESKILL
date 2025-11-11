package com.twotwo.logic;

import com.twotwo.ui.PlayerFrame;

// 所有玩家共有的操作
public class AllAction {
    // 添加地点选择的启动方法
    public static void startLocationSelection(Game game) {
        if (game.getIsProcessingLocationSelection())
            return; // 防止重复启动

        game.setIsProcessingLocationSelection(true);
        game.setLocationInputCount(0); // 重置计数器

        // 显示地点选择提示
        String locationHint = "请选择前往地点（1-龙穴，2-厨房，3-商店，4-图书馆）：";
        for (PlayerFrame pf : game.getPlayerFrames()) {
            if (pf.getPlayer().isAlive()) {
                // 存活玩家显示输入框
                pf.updateInfo(locationHint);
                pf.showInputArea();
            } else {
                // 已死亡玩家不参与
                pf.updateInfo("等待其他玩家选择地点...");
            }
        }
    }

}
