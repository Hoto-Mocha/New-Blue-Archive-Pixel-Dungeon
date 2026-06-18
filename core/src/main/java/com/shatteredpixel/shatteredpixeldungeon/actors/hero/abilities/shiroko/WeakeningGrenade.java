package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.shiroko;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.active.Grenade;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class WeakeningGrenade extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.SHIROKO_2;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;

        int dst = new Ballistica( hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID ).collisionPos;
        if (Actor.findChar(target) instanceof Hero || Actor.findChar(dst) instanceof Hero) {
            GLog.w(Messages.get(this, "cannot_self"));
            return;
        }

        new WeakeningGrenadeItem().knockItem().cast(hero, target);

        Gun gun = null;
        if (hero.belongings.weapon() instanceof Gun) gun = (Gun) hero.belongings.weapon();
        if (gun != null && hero.hasTalent(Talent.SHIROKO_ARMOR2_3)) {
            gun.manualReload(hero.pointsInTalent(Talent.SHIROKO_ARMOR2_3), true);
        }

        Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    public static class WeakeningGrenadeItem extends Grenade {

        @Override
        public int explodeMinDmg(){
            return Math.round((5 + Dungeon.scalingDepth()) * (1+0.25f*Dungeon.hero.pointsInTalent(Talent.SHIROKO_ARMOR2_1)));
        }

        @Override
        public int explodeMaxDmg(){
            return Math.round((10 + Dungeon.scalingDepth()*2) * (1+0.25f*Dungeon.hero.pointsInTalent(Talent.SHIROKO_ARMOR2_1)));
        }

        @Override
        public void effectsOnChar(Char ch) {
            Buff.affect(ch, Weakness.class, Math.round(10f * (1+0.25f*Dungeon.hero.pointsInTalent(Talent.SHIROKO_ARMOR2_1))));

            if (Dungeon.hero.hasTalent(Talent.SHIROKO_ARMOR2_2)) {
                float chance = 0.3f*Dungeon.hero.pointsInTalent(Talent.SHIROKO_ARMOR2_2);
                if (Random.Float() < chance) {
                    Buff.affect(ch, Blindness.class, 5f);
                }
                if (Random.Float() < chance-1) {
                    Buff.affect(ch, Paralysis.class, 5f);
                }
            }
        }

        @Override
        public Boomer knockItem() {
            return new WeakeningGrenadeBoomer();
        }

        public class WeakeningGrenadeBoomer extends Boomer {

            {
                image = ItemSpriteSheet.GRENADE;
            }

            @Override
            protected void activate(int cell) {
                explode(cell);
            }

            @Override
            public int throwPos(Hero user, int dst) {
                return new Ballistica( user.pos, dst, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID ).collisionPos;
            }

            @Override
            public float castDelay(Char user, int cell) {
                if (Random.Float() < 0.25f*Dungeon.hero.pointsInTalent(Talent.SHIROKO_ARMOR2_3)) {
                    return 0;
                } else {
                    return super.castDelay(user, cell);
                }
            }
        }
    }


    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SHIROKO_ARMOR2_1, Talent.SHIROKO_ARMOR2_2, Talent.SHIROKO_ARMOR2_3, Talent.HEROIC_ENERGY};
    }
}
