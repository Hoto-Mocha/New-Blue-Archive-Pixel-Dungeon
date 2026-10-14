package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class HealingTouch extends TargetedMariMiracle {
    public static HealingTouch INSTANCE = new HealingTouch();

    @Override
    public int icon() {
        return HeroIcon.HEALING_TOUCH;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.subClass == HeroSubClass.SYMBOL_OF_PEACE;
    }

    @Override
    protected void onTargetSelected(CrossNecklace cross, Hero hero, Integer target) {
        if (target == null) return;

        if (!Dungeon.level.heroFOV[target]) {
            hero.yellW("fov");
            return;
        }

        Char ch = Actor.findChar(target);

        if (ch == null) {
            hero.yellW("no_target");
            return;
        }

        hero.busy();
        hero.sprite.operate(target);
        Sample.INSTANCE.play(Assets.Sounds.READ);
        int HPLost = ch.HT - ch.HP;
        if (HPLost > 0){
            ch.heal(HPLost);
            NeutralDeferedDamage deferred = Buff.affect( ch, NeutralDeferedDamage.class );
            deferred.extend( HPLost );

            ch.sprite.showStatus( CharSprite.WARNING, Messages.get(Viscosity.class, "deferred", HPLost) );

            if (Dungeon.level.heroFOV[target]) {
                CellEmitter.get( target ).burst( Speck.factory( Speck.LIGHT ), 4 );
            }
        }
        if (ch != hero) {
            Buff.affect(hero, Barrier.class).setShield(HPLost);
        }
        hero.spendAndNext(Actor.TICK);
        onMiracleCast(cross, hero);
    }

    public static class NeutralDeferedDamage extends Viscosity.DeferedDamage {
        {
            type = buffType.NEUTRAL; //cannot be removed by cleansing effect
        }
    }
}
