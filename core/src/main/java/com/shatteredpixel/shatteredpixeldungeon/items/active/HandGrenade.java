package com.shatteredpixel.shatteredpixeldungeon.items.active;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HandGrenade extends Grenade {
    {
        image = ItemSpriteSheet.GRENADE;

        max_amount = 1;
        amount = max_amount;
        dropChance = 0.125f;
    }

    @Override
    public int explodeMinDmg() {
        int dmg = super.explodeMinDmg();
        if (Dungeon.hero != null) {
            if (Dungeon.hero.hasTalent(Talent.MIYAKO_T1_1)) dmg += Dungeon.hero.pointsInTalent(Talent.MIYAKO_T1_1)+1;
        }
        return dmg;
    }

    @Override
    public int explodeMaxDmg() {
        int dmg = super.explodeMaxDmg();
        if (Dungeon.hero != null) {
            if (Dungeon.hero.hasTalent(Talent.MIYAKO_T1_1)) dmg += Dungeon.hero.pointsInTalent(Talent.MIYAKO_T1_1)+1;
        }
        return dmg;
    }

    @Override
    public Grenade.Boomer knockItem(){
        return new HandGrenadeBoomer();
    }

    public class HandGrenadeBoomer extends Boomer {

        {
            image = ItemSpriteSheet.GRENADE;
        }

        //needs to be overridden
        @Override
        protected void activate(int cell) {
            explode(cell);
        }
    }
}
