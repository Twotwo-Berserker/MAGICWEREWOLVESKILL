package com.twotwo.util;

import javax.swing.*;

import com.twotwo.ui.PlayerFrame;

import java.awt.*;
import java.util.Timer;
import java.util.*;

/**
 * 倒计时工具类，用于在界面文本区域下方显示倒计时
 * 倒计时结束后会触发指定的回调方法
 */
public class CountdownUtil {

    /**
     * 在指定文本区域下方显示倒计时
     * 
     * @param textArea 显示文本的区域
     * @param seconds  倒计时秒数（10-30秒）
     * @param callback 倒计时结束后的回调方法
     */
    public static void startCountdown(PlayerFrame pf, JScrollPane scrollPane, int seconds, Runnable callback) {
        // 验证文本区域是否在滚动面板中（保持原验证逻辑）
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
        if (!(textArea instanceof JTextArea) || !(scrollPane.getParent() instanceof Container)) {
            throw new IllegalArgumentException("文本区域必须放在JScrollPane中,且JScrollPane必须在容器中");
        }

        // 保存原始文本内容（用于倒计时结束后恢复）
        Timer timer = new Timer();
        int[] remainingSeconds = { seconds }; // 用数组存储以在lambda中修改

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (remainingSeconds[0] > 0) {
                        // 1. 先移除所有已有的倒计时行
                        String currentText = removeExistingCountdown(textArea.getText());

                        // 2. 添加新的倒计时行
                        textArea.setText(currentText + "[倒计时] 剩余时间：" + remainingSeconds[0] + "秒\n");
                        remainingSeconds[0]--;

                        // 3. 自动滚动到底部
                        textArea.setCaretPosition(textArea.getDocument().getLength());

                        // 4. 界面刷新
                        pf.repaint();
                    } else {
                        // 倒计时结束：删除最后一行倒计时并执行回调
                        String currentText = removeExistingCountdown(textArea.getText());
                        textArea.setText(currentText + "倒计时结束！\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());

                        timer.cancel(); // 停止计时器
                        callback.run(); // 执行结束回调
                    }
                });
            }
        }, 0, 1000); // 立即开始，每秒执行一次
    }

    /**
     * 移除文本中所有包含倒计时的行
     * 
     * @param text 原始文本
     * @return 清除倒计时后的文本
     */
    private static String removeExistingCountdown(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 分割所有行（即使没有换行符，也按逻辑分割）
        String[] lines = text.split("\n");
        StringBuilder cleaned = new StringBuilder();

        for (String line : lines) {
            // 过滤掉包含"[倒计时]"的行（无论位置）
            if (!line.contains("[倒计时]")) {
                cleaned.append(line).append("\n"); // 保留非倒计时行，补全换行
            }
        }

        // 移除最后多余的换行符（如果有）
        if (cleaned.length() > 0) {
            cleaned.setLength(cleaned.length() - 1);
        }

        return cleaned.toString();
    }
}