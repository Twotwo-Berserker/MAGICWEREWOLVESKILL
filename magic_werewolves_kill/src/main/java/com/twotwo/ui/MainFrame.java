package com.twotwo.ui;

import javax.swing.*;
import java.awt.*;
import com.twotwo.logic.Game;
import com.twotwo.model.Player;
import com.twotwo.ui.*;
import com.twotwo.util.*;

public class MainFrame extends JFrame {
    public MainFrame(Game game) {
        setTitle("魔法狼人杀");
        setSize(400, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 主面板
        Container container = getContentPane();
        container.setLayout(null);
        container.setBackground(null);

        // 欢迎标签
        JLabel label = new JLabel("欢迎来到魔法狼人杀！");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(50, 10, 300, 30);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        container.add(label);

        // 开始游戏按钮
        JButton button_startgame = new JButton("开始游戏");
        button_startgame.setBounds(100, 120, 200, 50);
        container.add(button_startgame);

        button_startgame.addActionListener(e -> {
            // 隐藏主窗口
            setVisible(false);
            // 启动游戏流程（例如进入第一天）
            // game.addReadyPlayer();  // 之后多人游玩时使用
            game.startGame();
        });

        // 角色详情按钮
        JButton button_roledetails = new JButton("角色详情");
        button_roledetails.setBounds(100, 60, 200, 50);
        container.add(button_roledetails);

        button_roledetails.addActionListener(e -> {
            RoleDetails dlg = new RoleDetails(this);
            dlg.setVisible(true);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
