package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class Pray extends MariMiracle {

    public static final Pray INSTANCE = new Pray();

    @Override
    public float chargeUse(Hero hero) {
        return 0;
    }

    @Override
    public int icon() {
        return HeroIcon.PRAY;
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        hero.sprite.operate(hero.pos);
        GameScene.flash(0x88000000, false);
        Sample.INSTANCE.play(Assets.Sounds.READ);

        //we process this as 5x wait actions instead of one 5 tick action to prevent
        // effects like time freeze from eating the whole action duration
        for (int i = 0; i < 5; i++) hero.spendConstant(Actor.TICK);

        Buff.affect(hero, PrayResistance.class, hero.cooldown());
        Actor.addDelayed(new Actor() {

            {
                actPriority = VFX_PRIO;
            }

            @Override
            protected boolean act() {
                Buff.affect(hero, Recharging.class, 6f);
                Buff.affect(hero, ArtifactRecharge.class).extend(6f).ignoreHornOfPlenty = false;
                Actor.remove(this);
                onMiracleCast(cross, hero);
                return true;
            }
        }, hero.cooldown()-1);

        hero.next();
        hero.busy();
    }

    public static class PrayResistance extends FlavourBuff {
        {
            actPriority = HERO_PRIO+1; //ends just before the hero acts
        }
    };
}
