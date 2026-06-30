package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;

public class NoticeTracker extends Buff {
    {
        revivePersists = true;
        type = buffType.NEUTRAL;
    }

    public static boolean isNoticed() {
        boolean isNoticed = false;
        Hero hero = Dungeon.hero;
        if (hero.hasTalent(Talent.MIYU_T3_2)) {
            for (Char ch : Actor.chars()) {
                if (ch instanceof Mob && !(ch instanceof NPC)) {
                    if (((Mob) ch).isTargeting(hero) && ((Mob) ch).state == ((Mob) ch).HUNTING) {
                        isNoticed = true;
                        break;
                    }
                }
            }
        }
        return isNoticed;
    }
}
