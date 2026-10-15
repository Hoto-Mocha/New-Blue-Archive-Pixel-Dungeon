package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.MariTrinity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.watabou.utils.Callback;

public abstract class TrinityMiracle extends MariMiracle {
    @Override
    public float chargeUse(Hero hero) {
        return 6;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.buff(MariTrinity.TrinityBuff.class) != null && hero.armorAbility instanceof MariTrinity;
    }

    protected MariTrinity.TrinityBuff getBuff(Hero hero) {
        return hero.buff(MariTrinity.TrinityBuff.class);
    }

    public abstract void castEffect(Hero hero);

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
        castEffect(hero);
        onMiracleCast(cross, hero);
    }
}
