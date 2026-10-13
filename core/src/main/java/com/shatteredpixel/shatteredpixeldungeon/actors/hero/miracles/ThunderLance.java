package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.Thunder;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class ThunderLance extends TargetedMariMiracle {
    public static final ThunderLance INSTANCE = new ThunderLance();

    @Override
    public int icon() {
        return HeroIcon.THUNDER_LANCE;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3*amount(hero);
    }

    @Override
    public String desc() {
        int minBase = 2*Dungeon.hero.pointsInTalent(Talent.MARI_EX1_2);
        int maxBase = 10*Dungeon.hero.pointsInTalent(Talent.MARI_EX1_2);
        int min = minBase*amount(Dungeon.hero);
        int max = maxBase*amount(Dungeon.hero);
        int paralysis = amount(Dungeon.hero);
        return Messages.get(this, "desc", minBase, maxBase, min, max, paralysis) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_EX1_2) && amount(hero) > 0;
    }

    private int amount(Hero hero) {
        if (hero.buff(LightBullet.LightMagazine.class) == null) return 0;
        else return hero.buff(LightBullet.LightMagazine.class).bullets();
    }

    @Override
    protected void onTargetSelected(CrossNecklace cross, Hero hero, Integer target) {
        if (target == null) return;
        if (amount(hero) <= 0) return;

        ThunderItem thunder = new ThunderItem(cross, hero);
        thunder.cast(hero, target);
    }

    public class ThunderItem extends Item {
        {
            image = ItemSpriteSheet.THUNDER_LANCE;
        }

        CrossNecklace cross;
        Hero hero;

        public ThunderItem(CrossNecklace cross, Hero hero) {
            this.cross = cross;
            this.hero = hero;
        }

        @Override
        protected void onThrow(int cell) {
            Char ch = Actor.findChar( cell );
            if (ch != null) {
                int multi = amount(hero);
                int min = 2*hero.pointsInTalent(Talent.MARI_EX1_2)*multi;
                int max = 10*hero.pointsInTalent(Talent.MARI_EX1_2)*multi;
                int damage = Hero.heroDamageIntRange(min, max);
                if (hero.hasTalent(Talent.MARI_T1_3)) {
                    damage += 1+2*hero.pointsInTalent(Talent.MARI_T1_3);
                }
                ch.damage(damage, new ThunderLance());
                ch.sprite.burst(0xFFFFFFFF, 3);
                Thunder.thunderEffect(ch.sprite);
                Buff.affect(ch, Paralysis.class, amount(hero));
            } else {
                Dungeon.level.pressCell(cell);
            }

            onMiracleCast(cross, hero);
            Splash.at(cell, 0xFFFFFFFF, 3);
            hero.buff(LightBullet.LightMagazine.class).detach();
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
        }

        @Override
        public Emitter emitter() {
            Emitter emitter = new Emitter();
            emitter.pos( 5, 5, 0, 0);
            emitter.fillTarget = false;
            emitter.pour(SparkParticle.FACTORY, 0.04f);
            return emitter;
        }
    }
}
