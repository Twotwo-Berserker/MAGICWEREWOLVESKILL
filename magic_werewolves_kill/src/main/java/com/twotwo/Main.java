package com.twotwo;

import javax.swing.*;

import com.twotwo.logic.Game;
import com.twotwo.ui.*;
import com.twotwo.util.*;

import java.net.URL;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 初始化游戏状态
            Game game = new Game();
            game.initPlayers();

            // 2. 创建控制器窗口
            GameControllerFrame controllerFrame = new GameControllerFrame(game);

            // 3. 显示窗口
            MainFrame mainFrame = new MainFrame(game);
            // 从资源目录加载图标
            URL iconUrl = DataUtil.iconUrl;
            // 设置主窗口图标
            IconUtil.setWindowIcon(mainFrame, iconUrl);
            mainFrame.setVisible(true);
            controllerFrame.setVisible(true);
        });
    }
}
