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
        if (Talent.hasFoodTalent(Dungeon.hero)){
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
