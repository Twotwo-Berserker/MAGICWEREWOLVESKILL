package com.twotwo.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import com.twotwo.logic.Game;
import com.twotwo.util.IconUtil;

public class GameControllerFrame extends JFrame {
    private Game game;
    private JButton nextStepBtn;

    public GameControllerFrame(Game game) {
        this.game = game;
        setTitle("游戏控制器");
        setSize(300, 150);
        setLayout(new FlowLayout());

        nextStepBtn = new JButton("进行下一步");
        nextStepBtn.addActionListener(e -> {
            // game.processDay();
            // 通知所有玩家窗口更新信息
            // ...
        });
        add(nextStepBtn);

        // 设置窗口图标
        URL iconUrl = GameControllerFrame.class.getClassLoader().getResource("icons/app_icon.jpg");
        IconUtil.setWindowIcon(this, iconUrl);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
