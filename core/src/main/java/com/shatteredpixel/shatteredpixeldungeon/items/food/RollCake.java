package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mika.RollCakeThrow;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class RollCake extends Food {

    {
        image = ItemSpriteSheet.ROLL_CAKE;
        energy = Hunger.HUNGRY/6f; //50 food value

        bones = false;
    }

    @Override
    protected float eatingTime(){
        if (Talent.hasFoodTalent(Dungeon.hero)){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        hero.heal(3);
        if (hero.hasTalent(Talent.MIKA_ARMOR1_3)) {
            Buff.affect(hero, Haste.class, 2*hero.pointsInTalent(Talent.MIKA_ARMOR1_3));
        }
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof RollCakeThrow) {
            Char ch = Actor.findChar(cell);
            if (ch != null) {
                affectChar(ch);
            }
        }
        Splash.at(cell, 0x513231, 5);
        Sample.INSTANCE.play(Assets.Sounds.PLANT);
    }

    private void affectChar(Char ch) {
        ch.heal(10);
        Hero hero = Dungeon.hero;
        if (ch.alignment == Char.Alignment.ENEMY) {
            Buff.affect(ch, Slow.class, Slow.DURATION*2);
            if (hero.hasTalent(Talent.MIKA_ARMOR1_1)) {
                Buff.affect(ch, Blindness.class, 2*hero.pointsInTalent(Talent.MIKA_ARMOR1_1));
            }
            if (hero.hasTalent(Talent.MIKA_ARMOR1_2)) {
                Buff.affect(ch, Ooze.class).set(5*hero.pointsInTalent(Talent.MIKA_ARMOR1_2));
            }
        }
        if (hero.hasTalent(Talent.MIKA_ARMOR1_3)) {
            Buff.affect(hero, Haste.class, 2*hero.pointsInTalent(Talent.MIKA_ARMOR1_3));
        }
    }

    @Override
    public int value() {
        return 5 * quantity;
    }
}
