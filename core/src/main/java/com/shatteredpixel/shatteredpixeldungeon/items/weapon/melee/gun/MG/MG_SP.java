package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MG_SP extends MG {
    {
        image = ItemSpriteSheet.MG_SPECIAL;

        tier = 3;
        bones = false;
        unique = true;
    }

    @Override
    public int tier() {
        return super.tier() + ((Dungeon.hero != null) ? (int)(Dungeon.hero.lvl/20f) : 0);
    }

    @Override
    public int value() {
        return -1;
    }

    @Override
    public int buffedLvl() {
        return super.buffedLvl() + ((Dungeon.hero != null) ? (Math.max(0, Dungeon.hero.pointsInTalent(Talent.NONOMI_T3_1)-1)) : 0);
    }
}
