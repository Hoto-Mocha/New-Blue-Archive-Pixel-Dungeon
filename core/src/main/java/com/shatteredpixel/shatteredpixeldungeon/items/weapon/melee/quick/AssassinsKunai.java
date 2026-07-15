package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.quick;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class AssassinsKunai extends QuickWeapon {
    {
        tier = 1;
        image = ItemSpriteSheet.ASSASSIN_KUNAI;
        hitSound = Assets.Sounds.HIT_STAB;

        bones = false;
        unique = true;
    }

    public float assassinationMin(int lvl) {
        return (1+lvl)/(10f+lvl);
    }

    public float assassinationMax(int lvl) {
        return (1+lvl)/(3f+lvl);
    }


    public int assassinationMin(Mob enemy, int lvl) {
        return Math.round(enemy.HT*assassinationMin(lvl));
    }

    public int assassinationMax(Mob enemy, int lvl) {
        return Math.round(enemy.HT*assassinationMax(lvl));
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +
                lvl*(tier+1);
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.attackTarget();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                int baseDmg = super.damageRoll(owner);
                int bonusDmg = Hero.heroDamageIntRange(assassinationMin((Mob) enemy, buffedLvl()), assassinationMax((Mob) enemy, buffedLvl()));
                return baseDmg + bonusDmg;
            }
        }
        return super.damageRoll(owner);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) {
            defender.sprite.emitter().burst( ShadowParticle.UP, 5 );
        }
        return super.proc(attacker, defender, damage);
    }
}
