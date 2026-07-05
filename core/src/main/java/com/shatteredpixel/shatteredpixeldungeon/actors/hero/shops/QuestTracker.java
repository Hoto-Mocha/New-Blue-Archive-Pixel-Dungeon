package com.shatteredpixel.shatteredpixeldungeon.actors.hero.shops;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.QuestEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class QuestTracker extends YuzuShopContent {
     public static final QuestTracker INSTANCE = new QuestTracker();

    @Override
    public int icon() {
        return HeroIcon.SHOP_5;
    }

    @Override
    public void onSelect(Hero hero) {
        if (findQuestMob().isEmpty()) {
            GLog.w(Messages.get(this, "no_quest"));
            return;
        }

        for (Mob mob : findQuestMob()) {
            Buff.append(hero, TalismanOfForesight.CharAwareness.class, 10).charID = mob.id();
        }
        Dungeon.observe();
        hero.next();
    }

    @Override
    public boolean canSelect(Hero hero) {
        return super.canSelect(hero);
    }

    @Override
    public int creditUse(Hero hero) {
        return 50*inflationParameter();
    }

    @Override
    public boolean hideWindow() {
        return true;
    }

    public ArrayList<Mob> findQuestMob() {
        ArrayList<Mob> questMobs = new ArrayList<>();
        for (Char ch : Actor.chars()) {
            if (ch instanceof Mob && ch.buff(QuestEnemy.class) != null) {
                questMobs.add((Mob) ch);
            }
        }
        return questMobs;
    }
}
