package com.twotwo.util;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

// 图标工具类
public class IconUtil {
    // 通用设置窗口图标的方法
    public static void setWindowIcon(Window window, URL iconUrl) {
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            if (window instanceof JFrame) {
                ((JFrame) window).setIconImage(icon.getImage());
            } else if (window instanceof JDialog) {
                ((JDialog) window).setIconImage(icon.getImage());
            }
        } else {
            System.err.println("警告：未找到图标文件，请检查路径是否正确");
        }
    }
}