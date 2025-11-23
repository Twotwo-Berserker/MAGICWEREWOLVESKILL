package com.twotwo.model;

import com.twotwo.logic.Game;
import com.twotwo.logic.WinChecker;
import com.twotwo.model.Camp.CampType;
import com.twotwo.model.Location.LocationType;
import com.twotwo.model.Role.RoleType;
import com.twotwo.util.*;

public class Player {
    private RoleType role;
    private CampType camp;
    private boolean alive;
    private int dragonHearts; // 龙心数量
    private LocationType currentLocation; // 当前所在地点
    private boolean inWall; // 侦探是否在墙内
    private Player boundPlayer; // 厨娘绑定的玩家
    private boolean isBound; // 是否被绑定
    private boolean isGuarded; // 是否被护守护
    private boolean isDeceived; // 是否被妖精蒙蔽
    private boolean isDisturbed; // 是否被小原原干扰
    private int skillTimes; // 角色技能使用次数
    private int deathDay;
    // 其他状态变量...

    // 构造函数和getter/setter
    public Player(RoleType role) {
        this.role = role;
        this.camp = null;
        this.alive = true;
        this.dragonHearts = 0;
        this.currentLocation = null;
        this.inWall = false;
        this.boundPlayer = null;
        this.isBound = false;
        this.isGuarded = false;
        this.isDeceived = false;
        this.isDisturbed = false;
        this.skillTimes = 0;
        this.deathDay = -1;
    }

    public void setCamp(CampType camp) {
        this.camp = camp;
    }

    public RoleType getRole() {
        return role;
    }

    public CampType getCamp() {
        return camp;
    }

    public String getName() {
        return ChineseNameUtil.getChineseName(role);
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean GameAlive(Game game) {
        return alive || !alive && deathDay != game.getCurrentDay();
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getDragonHearts() {
        return dragonHearts;
    }

    public void addDragonHeart() {
        this.dragonHearts++;
    }

    public LocationType getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationType location) {
        this.currentLocation = location;
    }

    public boolean isInWall() {
        return inWall;
    }

    public void setInWall(boolean inWall) {
        this.inWall = inWall;
    }

    // 厨娘专属
    public void setBoundPlayer(Player boundPlayer) {
        this.boundPlayer = boundPlayer;
    }

    public Player getBoundPlayer() {
        return boundPlayer;
    }

    // 被绑定状态
    public boolean isBound() {
        return isBound;
    }

    public void setIsBound(boolean isBound) {
        this.isBound = isBound;
    }

    // 被守护状态
    public boolean isGuarded() {
        return isGuarded;
    }

    public void setIsGuarded(boolean isGuarded) {
        this.isGuarded = isGuarded;
    }

    // 被蒙蔽状态
    public boolean isDeceived() {
        return isDeceived;
    }

    public void setIsDeceived(boolean isDeceived) {
        this.isDeceived = isDeceived;
    }

    // 被干扰状态
    public boolean isDisturbed() {
        return isDisturbed;
    }

    public void setIsDisturbed(boolean isDisturbed) {
        this.isDisturbed = isDisturbed;
    }

    // 角色技能使用次数
    public int getSkillTimes() {
        return skillTimes;
    }

    public void incrementSkillTimes() {
        this.skillTimes++;
    }

    // 死亡天数
    public int getDeathDay() {
        return deathDay;
    }

    public void setDeathDay(int deathDay) {
        this.deathDay = deathDay;
    }

    // 玩家死亡处理，判断游戏胜负
    public void Die(Game game) {
        if (this.alive) {
            this.alive = false;
            this.deathDay = game.getCurrentDay();
            if (WinChecker.checkWerewolfWin(game) == 0) {
                game.setGameOver(0);
            }
            if (WinChecker.checkWerewolfWin(game) == 1) {
                game.setGameOver(1);
            }
        }
    }

}
