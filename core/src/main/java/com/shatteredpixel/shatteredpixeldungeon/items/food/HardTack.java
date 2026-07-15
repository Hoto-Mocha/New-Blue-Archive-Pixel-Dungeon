package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HardTack extends Food {

    {
        image = ItemSpriteSheet.HARD_BISCUIT;
        energy = Hunger.HUNGRY/3f; //100 food value

        bones = false;
    }

    @Override
    protected float eatingTime(){
        if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
                || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
                || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
                || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
                || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)
                || Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)
                || Dungeon.hero.hasTalent(Talent.ARIS_T2_1)
                || Dungeon.hero.hasTalent(Talent.NONOMI_T2_1)
                || Dungeon.hero.hasTalent(Talent.MIYAKO_T2_1)
                || Dungeon.hero.hasTalent(Talent.HOSHINO_T2_1)
                || Dungeon.hero.hasTalent(Talent.SHIROKO_T2_1)
                || Dungeon.hero.hasTalent(Talent.NOA_T2_1)
                || Dungeon.hero.hasTalent(Talent.MIYU_T2_1)
                || Dungeon.hero.hasTalent(Talent.YUZU_T2_1)
                || Dungeon.hero.hasTalent(Talent.IZUNA_T2_1)
        ){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        hero.heal(5);
        if (hero.belongings.weapon() instanceof Gun) {
            ((Gun) hero.belongings.weapon()).quickReload();
            ((Gun) hero.belongings.weapon()).manualReload(4, true);
        }
    }

    @Override
    public int value() {
        return 5 * quantity;
    }

}
