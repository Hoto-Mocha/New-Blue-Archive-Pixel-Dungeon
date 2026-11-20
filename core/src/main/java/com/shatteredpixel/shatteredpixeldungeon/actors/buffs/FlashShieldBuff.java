package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlashBangParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.FlashShield;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FlashShieldBuff extends Buff implements ActionIndicator.Action {

    public void checkShield(Hero hero) {
        if (!(hero.belongings.weapon instanceof FlashShield || hero.belongings.secondWep instanceof FlashShield)) {
            detach();
        }
    }

    @Override
    public boolean act() {
        if (!(target instanceof Hero)) detach();
        return super.act();
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
    public Visual primaryVisual() {
        Image icon;
        icon = new ItemSprite(ItemSpriteSheet.FLASH_SHIELD);
        return icon;
    }

    @Override
    public int indicatorColor() {
        return 0xFDE1AA;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(shooter);
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer cell ) {
            if (cell == null) return;

            Hero hero = Dungeon.hero;
            hero.busy();
            ArrayList<Char> affected = new ArrayList<>();

            Ballistica aim = new Ballistica(hero.pos, cell, Ballistica.WONT_STOP);
            int distance = 4;
            int degree = 120;
            ConeAOE aoe = new ConeAOE(aim, distance, degree, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);
            for (int c : aoe.cells) {
                if (Dungeon.level.heroFOV[c] && !Dungeon.level.solid[c]) {
                    CellEmitter.center(c).burst(FlashBangParticle.FACTORY, Random.IntRange(1, 3));
                }
                Char ch = Actor.findChar(c);
                if (ch != null) {
                    affected.add(ch);
                }
            }

            for (Char ch : affected) {
                if (ch instanceof Hero) {
                    continue;
                }
                Buff.affect(ch, Blindness.class, 5f);
            }
            GameScene.flash(0x60FFFFFF);
            int barrier = currentShieldBoost();
            if (barrier > 0) {
                Buff.affect(hero, Barrier.class).setShield(barrier);
            }
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            hero.spendAndNext(1f);
            if (hero.buff(FlashShieldBuff.class) != null) {
                hero.buff(FlashShieldBuff.class).detach();
                Buff.affect(target, FlashCooldown.class).set(50);
            }
            if (hero.belongings.weapon() instanceof Gun) {
                ((Gun)hero.belongings.weapon()).quickReload();
            }
            if (hero.belongings.secondWep()instanceof Gun) {
                ((Gun)hero.belongings.secondWep()).quickReload();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(FlashShieldBuff.class, "prompt");
        }
    };

    public int currentShieldBoost(){
        //base multiplier scales at 1/1.5/2/2.5/3x at 100/37/20/9/0% HP
        float shieldMultiplier = 1f + 2*(float)Math.pow((1f-(target.HP/(float)target.HT)), 3);

        int baseShield = 8;
        if (target instanceof Hero && (((Hero) target).belongings.weapon instanceof FlashShield || ((Hero) target).belongings.secondWep instanceof FlashShield)){
            FlashShield shield = ((Hero) target).belongings.getItem(FlashShield.class);
            baseShield += 2*shield.buffedLvl();
        }
        return Math.round(baseShield * shieldMultiplier);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        ActionIndicator.setAction(this);
        super.restoreFromBundle(bundle);
    }

    public static class FlashCooldown extends Buff {
        float maxDuration;
        float duration;

        {
            type = buffType.NEUTRAL;
        }

        public void set(float time) {
            maxDuration = time;
            duration = maxDuration+1;
        }

        public void hit() {
            duration -= 2 + Dungeon.hero.pointsInTalent(Talent.KURUMI_EX2_1);
            if (duration <= 0) {
                detach();
            }
            BuffIndicator.refreshHero();
        }

        public float duration() {
            return this.duration;
        }

        @Override
        public boolean act() {
            duration -= TICK;
            spend(TICK);
            if (duration <= 0) {
                detach();
            }
            BuffIndicator.refreshHero();
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xFDE1AA);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (maxDuration - duration) / maxDuration);
        }

        private static final String DURATION  = "duration";
        private static final String MAX_DURATION  = "maxDuration";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DURATION, duration);
            bundle.put(MAX_DURATION, maxDuration);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            duration = bundle.getFloat( DURATION );
            maxDuration = bundle.getFloat( MAX_DURATION );
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", 2 + Dungeon.hero.pointsInTalent(Talent.KURUMI_EX2_1), Messages.decimalFormat("#.##", duration));
        }

        @Override
        public void detach() {
            Buff.affect(target, FlashShieldBuff.class).checkShield((Hero) target);
            super.detach();
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)duration);
        }
    }
}
