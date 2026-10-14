package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Salvation extends MariMiracle {
    public static Salvation INSTANCE = new Salvation();

    @Override
    public int icon() {
        return HeroIcon.SALVATION;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 6;
    }

    @Override
    public String desc() {
        int turns = 1 + Dungeon.hero.pointsInTalent(Talent.MARI_EX2_2);
        return Messages.get(this, "desc", turns) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_EX2_2);
    }

    @Override
    public void onCast(CrossNecklace cross, Hero hero) {
        hero.busy();
        hero.sprite.operate(hero.pos, new Callback() {
            @Override
            public void call() {
                hero.sprite.idle();
                hero.spendAndNext(Actor.TICK);
            }
        });
        Sample.INSTANCE.play(Assets.Sounds.READ);
        for (Char ch : Actor.chars()){
            Buff.affect(ch, Invulnerability.class, 1+hero.pointsInTalent(Talent.MARI_EX2_2));
        }

        new Flare(5, 48).color(0xFFFF00, true).show(hero.sprite, 3f);
//        GameScene.flash(0x80FFFF40);
        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        onMiracleCast(cross, hero);
    }

}
