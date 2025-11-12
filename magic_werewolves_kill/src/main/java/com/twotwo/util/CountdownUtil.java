package com.twotwo.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 倒计时工具类，用于在界面文本区域下方显示倒计时
 * 倒计时结束后会触发指定的回调方法
 */
public class CountdownUtil {

    // 静态变量用于跟踪当前倒计时状态
    private Timer timer = null;
    private JLabel countdownLabel = null;
    private Container container = null;

    /**
     * 在指定文本区域下方显示倒计时
     * 
     * @param textArea 显示文本的区域
     * @param seconds  倒计时秒数（10-30秒）
     * @param callback 倒计时结束后的回调方法
     */
    public void startCountdown(JScrollPane scrollPane, int seconds, Runnable callback) {
        // 先停止当前可能存在的倒计时
        if (isCountingDown()) {
            finishCountdown();
        }

        // 校验秒数范围
        if (seconds < 10 || seconds > 300) {
            throw new IllegalArgumentException("倒计时秒数必须在10-300秒之间");
        }

        // 获取文本区域
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
        if (!(textArea instanceof JTextArea)) {
            throw new IllegalArgumentException("滚动面板中必须包含文本区域");
        }

        // 获取父容器
        Container grandParent = scrollPane.getParent();
        if (!(grandParent instanceof Container)) {
            throw new IllegalArgumentException("滚动面板必须有父容器");
        }

        this.container = (Container) grandParent;
        LayoutManager layout = container.getLayout();
        if (!(layout instanceof BorderLayout)) {
            throw new IllegalArgumentException("父容器必须使用BorderLayout布局");
        }

        // 创建倒计时标签
        this.countdownLabel = new JLabel("倒计时: " + seconds + "秒", SwingConstants.CENTER);
        this.countdownLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        this.countdownLabel.setForeground(Color.RED);

        // 添加到容器顶部
        this.container.add(this.countdownLabel, BorderLayout.NORTH);
        this.container.revalidate(); // 刷新布局
        this.container.repaint(); // 重绘容器

        // 创建定时器
        timer = new Timer(1000, new ActionListener() {
            private int remainingSeconds = seconds;

            @Override
            public void actionPerformed(ActionEvent e) {
                remainingSeconds--;
                if (remainingSeconds > 0) {
                    countdownLabel.setText("倒计时: " + remainingSeconds + "秒");
                } else {
                    // 倒计时结束
                    countdownLabel.setText("倒计时结束！");
                    finishCountdown();
                    if (callback != null) {
                        SwingUtilities.invokeLater(callback);
                    }
                }
            }
        });

        timer.start();
    }

    /*
     * 检查是否有正在进行的倒计时
     */
    public boolean isCountingDown() {
        // 定时器不为null且正在运行时，视为正在倒计时
        return timer != null && timer.isRunning();
    }

    /*
     * 倒计时结束处理
     */
    public void finishCountdown() {
        timer.stop();
        // 延迟移除标签，让用户看到结束提示
        SwingUtilities.invokeLater(() -> {
            removeLabel();
            resetVariables();
        });
    }

    /**
     * 移除倒计时标签
     */
    private void removeLabel() {
        if (container != null && countdownLabel != null) {
                container.remove(countdownLabel);
                container.revalidate();
                container.repaint();
            }
    }

    /**
     * 重置静态变量（内部辅助方法）
     */
    private void resetVariables() {
        timer = null;
        countdownLabel = null;
        container = null;
    }

}