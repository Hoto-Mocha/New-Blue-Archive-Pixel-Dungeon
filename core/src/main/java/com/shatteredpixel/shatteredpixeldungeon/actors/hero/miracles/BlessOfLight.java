package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class BlessOfLight extends MariMiracle {
    public static BlessOfLight INSTANCE = new BlessOfLight();

    @Override
    public int icon() {
        return HeroIcon.BLESS_OF_LIGHT;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero)
                && hero.hasTalent(Talent.MARI_ARMOR1_2)
                && hero.buff(LightBullet.LightMagazine.class) != null
                && hero.buff(BlessOfLightBuff.class) == null;
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        hero.busy();
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.next();
            }
        });
        Sample.INSTANCE.play(Assets.Sounds.READ);
        Buff.affect(hero, BlessOfLightBuff.class);
        onMiracleCast(cross, hero);
    }

    public static class BlessOfLightBuff extends Buff {
        @Override
        public boolean act() {
            if (target.buff(LightBullet.LightMagazine.class) == null) detach();
            return super.act();
        }

        public void proc(Hero hero, Char ch, int damage) {
            if (ch != null) {
                hero.heal(Math.round(0.4f*damage));
                ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
            }
        }
    }
}
