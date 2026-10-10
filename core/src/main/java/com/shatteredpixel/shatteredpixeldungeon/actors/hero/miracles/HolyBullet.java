package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

public class HolyBullet extends TargetedMariMiracle {
    public static final HolyBullet INSTANCE = new HolyBullet();

    @Override
    public int icon() {
        return HeroIcon.HOLY_BULLET;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 5;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Dungeon.hero.lvl+5) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
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
        HolyBulletItem holyBulletItem = new HolyBulletItem(cross, hero);
        holyBulletItem.cast(hero, target);
    }

    @Override
    public void onMiracleCast(CrossNecklace cross, Hero hero) {
        super.onMiracleCast(cross, hero);

        Gun gun = getEquippedGun(hero);
        if (gun != null) gun.manualReload();
    }

    public class HolyBulletItem extends Item {
        {
            image = ItemSpriteSheet.HOLY_BULLET;
        }

        CrossNecklace cross;
        Hero hero;

        public HolyBulletItem(CrossNecklace cross, Hero hero) {
            this.cross = cross;
            this.hero = hero;
        }

        @Override
        protected void onThrow(int cell) {
            Char ch = Actor.findChar( cell );
            Gun gun = getEquippedGun(hero);

            if (ch != null) {
                int damage = Hero.heroDamageIntRange(10, 20);
                if (hero.buff(LightBullet.LightMagazine.class) != null) {
                    hero.buff(LightBullet.LightMagazine.class).onShoot(hero);
                    damage += hero.lvl+5;
                }
                if (Char.hasProp(ch, Char.Property.DEMONIC) || Char.hasProp(ch, Char.Property.UNDEAD)) {
                    ch.sprite.emitter().burst( ShadowParticle.UP, 6 );
                    Sample.INSTANCE.play(Assets.Sounds.BURNING);
                    damage = Math.round(damage*1.5f);
                }
                ch.damage(damage, new LightBullet());
                Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
                ch.sprite.burst(0xFF2CFFE7, 3);
                if (gun != null) {
                    if (!ch.isAlive()) {
                        gun.quickReload();
                    }
                }

            } else {
                Dungeon.level.pressCell(cell);
            }

            onMiracleCast(cross, hero);
            Splash.at(cell, 0xFF2CFFE7, 3);
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
        }

        @Override
        public Emitter emitter() {
            Emitter emitter = new Emitter();
            emitter.pos( 5, 5, 0, 0);
            emitter.fillTarget = false;
            emitter.pour(SparkParticle.FACTORY, 0.01f);
            return emitter;
        }
    }
}
