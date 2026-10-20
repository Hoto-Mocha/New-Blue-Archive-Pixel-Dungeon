package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class CharityKiss extends TargetedMariMiracle {
    public static CharityKiss INSTANCE = new CharityKiss();

    @Override
    public int icon() {
        return HeroIcon.CHARITY_KISS;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 10;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_ARMOR3_2);
    }

    @Override
    protected void onTargetSelected(CrossNecklace cross, Hero hero, Integer target) {
        if (target == null || target == hero.pos) return;

        if (!Dungeon.level.heroFOV[target]) {
            hero.yellW("fov");
        }

        Char ch = Actor.findChar(target);
        if (ch == null) {
            hero.yellW("no_target");
            return;
        }

        if (ch.alignment == hero.alignment || !(ch instanceof Mob)) {
            hero.yellW("not_enemy");
            return;
        }

        if (ch.isImmune(AllyBuff.class)) {
            hero.yellW("immune");
            return;
        }

        AllyBuff.affectAndLoot((Mob)ch, hero, ScrollOfSirensSong.Enthralled.class);
        AllyBuff.affect(ch, Adrenaline.class, 3*hero.pointsInTalent(Talent.MARI_ARMOR3_2));

        onMiracleCast(cross, hero);
    }
}
