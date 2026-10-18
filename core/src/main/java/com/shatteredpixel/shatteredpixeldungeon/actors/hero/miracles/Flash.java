package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Flash extends MariMiracle {
    public static Flash INSTANCE = new Flash();

    @Override
    public int icon() {
        return HeroIcon.FLASH_MIRACLE;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public String desc() {
        int blind = Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR1_1);
        int light = 10*Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR1_1);
        return Messages.get(this, "desc", blind, light) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero)
                && hero.hasTalent(Talent.MARI_ARMOR1_1)
                && hero.buff(LightBullet.LightMagazine.class) != null
                && hero.buff(FlashBuff.class) == null;
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
        Buff.affect(hero, FlashBuff.class);
        onMiracleCast(cross, hero);
    }

    public static class FlashBuff extends Buff {
        @Override
        public boolean act() {
            if (target.buff(LightBullet.LightMagazine.class) == null) detach();
            return super.act();
        }

        public void proc(Hero hero, Char ch) {
            if (ch != null) {
                Buff.prolong(hero, Light.class, 10f*hero.pointsInTalent(Talent.MARI_ARMOR1_1));
                Buff.affect(ch, Blindness.class, hero.pointsInTalent(Talent.MARI_ARMOR1_1));

                CellEmitter.get( ch.pos ).burst( Speck.factory( Speck.LIGHT ), 4 );
            }
            detach();
        }
    }
}
