package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.quick;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class QuickWeapon extends MeleeWeapon {

    public static final String AC_ATTACK = "ATTACK";

    {
        defaultAction = AC_ATTACK;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ATTACK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ATTACK)) {
            usesTargeting = true;
            curUser = hero;
            curItem = this;
            GameScene.selectCell(attacker);
        }
    }

    private CellSelector.Listener attacker = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                Char ch = Actor.findChar(target);
                Hero hero = curUser;
                if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                    if (!canReach(hero, target)) {
                        GLog.w(Messages.get(QuickWeapon.class, "cannot_reach"));
                    } else if (hero.isCharmedBy(ch)) {
                        GLog.w( Messages.get(Charm.class, "cant_attack"));
                    } else {
                        KindOfWeapon herosWeapon = hero.belongings.weapon; //기존에 사용하던 무기를 저장
                        hero.belongings.weapon = QuickWeapon.this; //공격에 사용할 무기를 이 무기로 변경
                        hero.busy();
                        hero.curAction = new HeroAction.Attack( ch ); //영웅이 대상을 공격함
                        Buff.affect(hero, QuickWeaponTracker.class).setWeapon(herosWeapon); //공격 후 영웅의 무기를 원래대로 되돌리도록 지연시키는 버프
                        hero.next();
                    }
                } else {
                    GLog.w(Messages.get(QuickWeapon.class, "no_enemy"));
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(QuickWeapon.class, "prompt");
        }
    };

    public static class QuickWeaponTracker extends Buff {
        {
            actPriority = HERO_PRIO-1;
        }

        KindOfWeapon weapon;

        public void setWeapon(KindOfWeapon weapon) {
            this.weapon = weapon;
        }

        @Override
        public boolean act() {
            Dungeon.hero.belongings.weapon = weapon; //영웅의 무기를 원래 무기로 되돌림
            Item.updateQuickslot();
            detach();
            return true;
        }

        private static final String WEAPON = "weapon";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(WEAPON, weapon);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            weapon = (KindOfWeapon)bundle.get(WEAPON);
        }
    }
}
