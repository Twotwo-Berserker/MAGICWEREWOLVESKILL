package com.twotwo.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.twotwo.ui.PlayerFrame;

// 用于处理回车键事件的工具类
public class EnterKeyUtil {
    public static void pressEnterKey(PlayerFrame pf, JButton button) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        pf.getRootPane().setDefaultButton(button);
    }
}


