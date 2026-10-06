package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class LightBullet extends TargetedMariMiracle {

    public static final LightBullet INSTANCE = new LightBullet();

    @Override
    public int icon() {
        return HeroIcon.LIGHT_BULLET;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2;
    }

    @Override
    protected void onTargetSelected(CrossNecklace cross, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        Ballistica aim = new Ballistica(hero.pos, target, targetingFlags());

        if (Actor.findChar( aim.collisionPos ) == hero){
            GLog.i( Messages.get(Wand.class, "self_target") );
            return;
        }

        if (Actor.findChar(aim.collisionPos) != null) {
            QuickSlotButton.target(Actor.findChar(aim.collisionPos));
        } else {
            QuickSlotButton.target(Actor.findChar(target));
        }

        hero.busy();
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
        hero.sprite.zap(target);
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.LIGHT_MISSILE, hero.sprite, aim.collisionPos, new Callback() {
            @Override
            public void call() {

                Char ch = Actor.findChar( aim.collisionPos );
                if (ch != null) {
                    ch.damage(Hero.heroDamageIntRange(2, 8), LightBullet.this);
                    Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
                    ch.sprite.burst(0xFFFFFF44, 3);
                } else {
                    Dungeon.level.pressCell(aim.collisionPos);
                }

                hero.spend( 1f );
                hero.next();

                onMiracleCast(cross, hero);
//                if (hero.subClass == HeroSubClass.PRIEST && hero.buff(GuidingLight.GuidingLightPriestCooldown.class) == null) {
//                    Buff.prolong(hero, GuidingLight.GuidingLightPriestCooldown.class, 50f);
//                    ActionIndicator.refresh();
//                }

            }
        });
    }

    @Override
    public void onMiracleCast(CrossNecklace cross, Hero hero) {
        super.onMiracleCast(cross, hero);

        Buff.affect(hero, LightMagazine.class).set(getEquippedGun(hero), 1);

        Gun gun = getEquippedGun(hero);
        if (gun != null) gun.manualReload();
    }

    public static class LightMagazine extends Buff {

        @Override
        public int icon() {
            return BuffIndicator.LIGHT_BULLET;
        }

        private Gun gun;
        private int bullets;

        public void set(Gun gun, int bullets) {
            this.gun = gun;
            if (bullets > this.bullets) {
                this.bullets = bullets;
            }
        }

        @Override
        public boolean act() {
            if (this.gun == null) detach();
            if (Dungeon.hero != null && Dungeon.hero.belongings.weapon != this.gun) {
                detach();
            }
            spend(TICK);
            return super.act();
        }

        public void onShoot(Hero hero) {
            bullets--;
            if (bullets <= 0) {
                detach();
            }
        }

        public int proc(Hero attacker, Char defender, int damage) {
            if (attacker.hasTalent(Talent.MARI_T1_3)) {
                damage += 1+attacker.pointsInTalent(Talent.MARI_T1_3);
            }
            return damage;
        }

        private static final String GUN = "gun";
        private static final String BULLETS = "bullets";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(GUN, gun);
            bundle.put(BULLETS, bullets);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            gun = (Gun)bundle.get(GUN);
            bullets = bundle.getInt(BULLETS);

        }
    }
}
