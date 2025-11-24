package com.twotwo.util;

import java.util.*;
import java.util.stream.Stream;

import com.twotwo.logic.*;
import com.twotwo.model.*;
import com.twotwo.ui.PlayerFrame;

// 生成玩家列表字符串
public class PlayerListUtil {
    // 所有玩家
    public static String getPlayerList(List<Player> players) {
        StringBuilder sb = new StringBuilder("玩家列表：\n");
        int index = 1;
        for (Player p : players) {
            sb.append(index).append(". ")
                    .append(p.getName())
                    .append("\n");
            index++;
        }
        // 去除最后一个换行
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // 所有玩家中下标为index的玩家
    public static Player getPlayer(List<Player> players, int index) {
        if (index < 1 || index > players.size()) {
            return null;
        }
        return players.get(index - 1);
    }

    // 除自身外的所有玩家
    public static String getOtherPlayerList(List<Player> players, Player self) {
        StringBuilder sb = new StringBuilder("其他玩家列表：\n");
        int index = 1;
        for (Player p : players) {
            if (p.getRole() != self.getRole()) {
                sb.append(index).append(". ")
                        .append(p.getName())
                        .append("\n");
                index++;
            }
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // 除自身外的所有玩家中下标为index的玩家
    public static Player getOtherPlayer(List<Player> players, Player self, int index) {
        int count = 0;
        for (Player p : players) {
            if (p != self) {
                count++;
                if (count == index) {
                    return p;
                }
            }
        }
        return null;
    }

    // 存活玩家
    public static String getAlivePlayerList(List<Player> players) {
        StringBuilder sb = new StringBuilder("存活玩家列表：\n");
        int index = 1;
        for (Player p : players) {
            if (p.isAlive()) {
                sb.append(index).append(". ")
                        .append(p.getName())
                        .append("\n");
                index++;
            }
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // 存活玩家中下标为index的玩家
    public static Player getAlivePlayer(List<Player> players, int index) {
        int count = 0;
        for (Player p : players) {
            if (p.isAlive()) {
                count++;
                if (count == index) {
                    return p;
                }
            }
        }
        return null;
    }

    // 除自身外的存活玩家
    public static String getOtherAlivePlayerList(List<Player> players, Player self) {
        StringBuilder sb = new StringBuilder("其他存活玩家列表：\n");
        int index = 1;
        for (Player p : players) {
            if (p.isAlive() && p.getRole() != self.getRole()) {
                sb.append(index).append(". ")
                        .append(p.getName())
                        .append("\n");
                index++;
            }
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // 除自身外的存活玩家中下标为index的玩家
    public static Player getOtherAlivePlayer(List<Player> players, Player self, int index) {
        int count = 0;
        for (Player p : players) {
            if (p.isAlive() && p != self) {
                count++;
                if (count == index) {
                    return p;
                }
            }
        }
        return null;
    }

    // 同一地点的玩家列表
    public static List<Player> getSameLocationPlayerList(List<Player> players, Player self, int SeeDetective) {
        List<Player> sameLocationPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.getCurrentLocation() == self.getCurrentLocation() && p != self) {
                if (SeeDetective == 0 && p.getRole() == Role.RoleType.DETECTIVE)
                    continue;
                sameLocationPlayers.add(p);
            }
        }
        return sameLocationPlayers;
    }

    public static String getSameLocationPlayerListToString(List<Player> sameLocationPlayers) {
        StringBuilder sb = new StringBuilder("当前地点玩家列表：\n");
        int index = 1;
        for (Player sp : sameLocationPlayers) {
            sb.append(index).append(". ")
                    .append(sp.getName())
                    .append("\n");
            index++;
        }
        if(sameLocationPlayers.isEmpty()) {
            return "当前地点无其他玩家。";
        }
        return sb.toString();
    }

    public static Player getSameLocationPlayer(List<Player> sameLocationPlayers, int index) {
        return sameLocationPlayers.get(index - 1);
    }

    // 当天死亡玩家列表
    public static String getTodayDeadPlayerList(Game game) {
        List<Player> players = game.getPlayers();
        StringBuilder sb = new StringBuilder("今日死亡玩家列表：\n");
        int index = 1;
        for (Player p : players) {
            if (!p.isAlive() && p.getDeathDay() == game.getCurrentDay()) {
                sb.append(index).append(". ")
                        .append(p.getName())
                        .append("\n");
                index++;
            }
        }
        sb.setLength(sb.length() - 1);
        // 如果无人死亡
        if (index == 1) {
            return "今日无人死亡。";
        }
        return sb.toString();
    }

    // 过去死亡玩家列表
    public static String getPastDeadPlayerList(Game game) {
        List<Player> players = game.getPlayers();
        StringBuilder sb = new StringBuilder("死亡玩家列表：\n");
        int index = 1;
        for (Player p : players) {
            if (!p.isAlive() && p.getDeathDay() != game.getCurrentDay()) {
                sb.append(index).append(". ")
                        .append(p.getName())
                        .append("\n");
                index++;
            }
        }
        sb.setLength(sb.length() - 1);
        // 如果无人死亡
        if (index == 1) {
            return "所有玩家均存活。";
        }
        return sb.toString();
    }

    public static Player getPastDeadPlayer(Game game, int index) {
        List<Player> players = game.getPlayers();
        int count = 0;
        for (Player p : players) {
            if (!p.isAlive() && p.getDeathDay() != game.getCurrentDay()) {
                count++;
                if (count == index) {
                    return p;
                }
            }
        }
        return null;
    }

}