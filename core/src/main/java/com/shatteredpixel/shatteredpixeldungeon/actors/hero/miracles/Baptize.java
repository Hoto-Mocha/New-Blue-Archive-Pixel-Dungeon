package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LightPillarParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Baptize extends TargetedMariMiracle {
    public static Baptize INSTANCE = new Baptize();

    @Override
    public int icon() {
        return HeroIcon.BAPTIZE;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public String desc() {
        Barrier buff = Dungeon.hero.buff(Barrier.class);
        int damage;
        if (buff == null) damage = 0;
        else damage = Math.round(buff.shielding()*0.5f*Dungeon.hero.pointsInTalent(Talent.MARI_EX2_1));
        return Messages.get(this, "desc", damage) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_EX2_1) && hero.buff(Barrier.class) != null;
    }

    @Override
    protected void onTargetSelected(CrossNecklace cross, Hero hero, Integer target) {
        if (target == null) return;

        if (!Dungeon.level.heroFOV[target]) {
            hero.yellW("fov");
            return;
        }

        Char ch = Char.findChar(target);

        if (ch == null) {
            hero.yellW("no_target");
            return;
        }

        if (ch == hero || ch.alignment == hero.alignment) {
            hero.yellW("not_enemy");
            return;
        }

        hero.busy();
        ScrollOfRecharging.charge(hero);
        hero.sprite.zap(target, new Callback() {
            @Override
            public void call() {
                hero.next();
                hero.sprite.idle();
            }
        });
        Barrier buff = hero.buff(Barrier.class);
        int damage = Math.round(buff.shielding() * 0.5f * hero.pointsInTalent(Talent.MARI_EX2_1));

        ch.damage(damage, this);
        ch.sprite.flash();
        int dots = 3+2*damage/10;
        int[] colors = { 0xFFFFFF, 0xFFF0A0, 0xFFE066, 0xFFD700 };
        for (int i = 0; i < dots; i++) {
            int color = colors[Random.Int( colors.length )];
            Splash.at(target, color, 1);
        }
        Sample.INSTANCE.play(Assets.Sounds.BLAST);

        buff.detach();

        Emitter emitter = ch.sprite.emitter();
        float feetX = ch.sprite.center().x;
        float feetY = ch.sprite.y + ch.sprite.height();
        emitter.pos( feetX, feetY, 0, 0 );
        emitter.burst(LightPillarParticle.FACTORY, 1);

        onMiracleCast(cross, hero);
    }
}
