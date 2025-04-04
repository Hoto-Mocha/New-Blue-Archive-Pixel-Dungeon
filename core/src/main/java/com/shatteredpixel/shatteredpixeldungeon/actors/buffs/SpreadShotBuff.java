package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

import java.util.ArrayList;

public class SpreadShotBuff extends Buff implements ActionIndicator.Action {
    {
        revivePersists = true;
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SPREAD_SHOT_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0xCBB994;
    }

    @Override
    public void doAction() {
        Hero hero = (Hero) target;
        if (!(hero.belongings.weapon() instanceof Gun)) {
            hero.yellW(Messages.get(Hero.class, "nonomi_spread_no_gun"));
            return;
        }
        if (((Gun)hero.belongings.weapon()).round() <= 0) {
            ((Gun)hero.belongings.weapon()).reload();
            return;
        }
        hero.belongings.weapon().usesTargeting = true;
        GameScene.selectCell(shooter);
    }

    private void spreadShot(int target, Gun gun) {
        Hero hero = (Hero) this.target;
        Ballistica aim = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET);
        int dist = Math.min(5, aim.dist);
        int degrees = Math.round((30+10*hero.pointsInTalent(Talent.NONOMI_EX2_1))*(6/(float)dist));
        degrees = Math.min(degrees, 180); //최대 180도
        ConeAOE cone = new ConeAOE(aim,
                dist,
                degrees,
                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);
        for (int cell : cone.cells){
            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
            Char ch = Actor.findChar(cell);
            if (ch != null) {
                gun.knockBullet().shoot(cell, false);
            };
        }
        gun.useRound();
        gun.knockBullet().throwSound();
        hero.sprite.zap(target);
        hero.spendAndNext(gun.knockBullet().delayFactor(hero));
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            Hero hero = Dungeon.hero;
            if (target != null && hero.belongings.weapon() instanceof Gun) {
                if (target == hero.pos) {
                    hero.belongings.weapon().usesTargeting = false;
                    ((Gun) hero.belongings.weapon()).reload();
                } else {
                    spreadShot(target, (Gun) hero.belongings.weapon());
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };
}
