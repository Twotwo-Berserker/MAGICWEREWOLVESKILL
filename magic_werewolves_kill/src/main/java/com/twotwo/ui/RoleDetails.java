package com.twotwo.ui;

import javax.swing.*;
import java.awt.*;

public class RoleDetails extends JDialog  {
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
                + "1. 大小姐：绝对不会是狼，聊天室阶段决定发言顺序，最后一个发言。死后虽无视角，且无法发言，但仍可以继续决定发言顺序。一局有一次激光，发射后聊天和投票流程不断，若护死亡可补充一次，只可以在聊天室投票前杀人，且不可杀护。\n\n"
                + "2. 护：每天可以守护一个人绝对不会死亡（投票除外）且视野不被遮蔽（防了一手妖精），不能连续两天守同一个人。\n\n"
                + "3. 魔女：白天可以消耗行动点使用一次光波，发射光波会公示。若杀到队友则自己死亡。\n\n"
                + "4. 侦探：白天等其他人选择完地点后再选，可以消耗行动点躲入墙内。若前往地点已满人则自动发动技能。在墙内无法目击狼刀，也无法发动狼刀。另外，狼可以选择杀墙。\n\n"
                + "5. 小原原：每天投票前选定一个玩家，使其投票无效，自己投票x2。\n\n"
                + "6. 蓝金：可在白天消耗行动点转移位置，相当于知道两个地点有什么人，转移后地点的玩家也能发现蓝金的存在。行动晚于狼刀，所以无法躲狼刀。\n\n"
                + "7. 白雪：进聊天室前可以验一个玩家白天第一个选择的地点及该地点的所有人，可以验出侦探。\n\n"
                + "8. 仓鼠：可以自爆带走周围一人（白天行动或聊天时均可），自爆后会公示。\n\n"
                + "9. 人偶师：白天可以消耗一行动点复活一位死者，当晚死而复生者回归聊天室。\n\n"
                + "10. 厨娘：开局选择一位玩家绑定，绑定者不知道。若厨娘为好人，则与绑定者选择同一地点时可以免疫狼刀；若厨娘为狼人，则绑定者存活时免疫激光。\n\n"
                + "11. 妖精：每天可以遮蔽一个人的视线，让其无法知道所在地点有谁且失去当日行动点。\n";

        area.setText(text);

        JScrollPane scroll = new JScrollPane(area);
        container.add(scroll, BorderLayout.CENTER);
    }
}
