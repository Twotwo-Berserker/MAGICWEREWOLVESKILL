package com.twotwo.util;

import javax.swing.*;

public class MessageUtil {
    // 显示信息对话框
    public static void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "信息", JOptionPane.INFORMATION_MESSAGE);
    }

    // 显示警告对话框
    public static void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "警告", JOptionPane.WARNING_MESSAGE);
    }

    // 显示错误对话框
    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}