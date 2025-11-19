package com.twotwo.ui;

import javax.swing.*;
import java.awt.*;

public class RoleDetails extends JDialog {
    public RoleDetails(JFrame owner) {
        super(owner, "角色详情", true); // 指定 owner 并设置模态
        setSize(520, 420);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        Container container = getContentPane();
        container.setLayout(new BorderLayout(8, 8));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        String text = ""
                + "1. 大小姐：绝对不会是狼，聊天室阶段决定发言顺序，最后一个发言。死后虽无视角，且无法发言，但仍可以继续决定发言顺序。一局有一次激光，发射后聊天和投票流程不断，只可以在聊天室投票前杀人，且不可杀护。\n\n"
                + "2. 护：每天可以守护一个人绝对不会死亡（投票除外）且视野不被遮蔽（防了一手妖精），不能连续两天守同一个人。\n\n"
                + "3. 魔女：白天可以消耗行动点使用一次光波，发射光波会公示。若杀到队友则自己死亡。\n\n"
                + "4. 侦探：白天等其他人选择完地点后再选，也就拥有了每个地点的人数信息。选后可以消耗行动点躲入墙内。若前往地点已满人则自动消耗行动点躲入墙内。在墙内能看到在场其他人，但无法行动或发动狼刀。另外，狼可以选择杀墙。\n\n"
                + "5. 小原原：每天投票前选定一个玩家，使其投票无效，自己投票x2。一局2次。\n\n"
                + "6. 蓝金：被动技能，若选择地点存在狼人，立刻警觉。每局第一次警觉时，免疫狼刀并打印当地所有玩家。\n\n"
                + "7. 白雪：进聊天室前可以查验一个玩家是否为狼。\n\n"
                + "8. 仓鼠：可以自爆带走周围一人（白天行动或聊天时均可），自爆后会公示。\n\n"
                + "9. 人偶师：白天可以消耗一行动点复活一位死者，当晚死而复生者回归聊天室。一局一次。\n\n"
                + "10. 厨娘：开局选择一位玩家绑定，绑定者不知道。若厨娘为好人，则与绑定者选择同一地点时可以免疫狼刀；若厨娘为狼人，则绑定者存活时免疫激光。\n\n"
                + "11. 妖精：可以遮蔽一个人的视线，让其失去当日动刀机会和行动点。一局2次。\n";

        area.setText(text);

        JScrollPane scroll = new JScrollPane(area);
        container.add(scroll, BorderLayout.CENTER);
    }
}
