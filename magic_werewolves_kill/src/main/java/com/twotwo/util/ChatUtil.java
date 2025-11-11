package com.twotwo.util;

import com.twotwo.ui.PlayerFrame;
import java.util.*;
import javax.swing.SwingUtilities;

/**
 * 聊天工具类，用于处理玩家间的私聊和公开发言功能
 */
public class ChatUtil {

    /**
     * 实现两个玩家界面间的私聊功能
     * @param senderFrame 发送方界面
     * @param receiverFrame 接收方界面
     * @param message 聊天内容
     */
    public static void privateChat(PlayerFrame senderFrame, PlayerFrame receiverFrame, String message) {
        // 确保在Swing事件调度线程中执行UI操作
        SwingUtilities.invokeLater(() -> {
            senderFrame.showInputArea();
            String senderName = senderFrame.getPlayer().getName();
            String receiverName = receiverFrame.getPlayer().getName();
            
            // 向发送方显示私聊信息（包含接收方提示）
            senderFrame.updateInfo("[私聊给 " + receiverName + "]：" + message);
            
            // 向接收方显示私聊信息（包含发送方提示）
            receiverFrame.updateInfo("[来自 " + senderName + " 的私聊]：" + message);
        });
    }

    /**
     * 实现公开发言功能（所有存活玩家都能看到）
     * @param allFrames 所有玩家界面列表
     * @param senderFrame 发言玩家界面
     * @param message 发言内容
     */
    public static void publicChat(List<PlayerFrame> allFrames, PlayerFrame senderFrame, String message) {
        // 确保在Swing事件调度线程中执行UI操作
        SwingUtilities.invokeLater(() -> {
            String senderName = senderFrame.getPlayer().getName();
            String publicMessage = "[" + senderName + " 发言]：" + message;
            
            // 向所有存活玩家发送公聊信息
            for (PlayerFrame frame : allFrames) {
                if (frame.getPlayer().isAlive()) { // 只给存活玩家显示
                    frame.updateInfo(publicMessage);
                }
            }
        });
    }
}