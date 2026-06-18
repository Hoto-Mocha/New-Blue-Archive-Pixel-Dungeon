package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.shiroko;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.active.Grenade;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

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

        Invisibility.dispel();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
    }

    public static class WeakeningGrenadeItem extends Grenade {

        @Override
        public void effectsOnChar(Char ch) {
            Buff.affect(ch, Weakness.class, 10f);
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
        }
    }


    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SHIROKO_ARMOR1_1, Talent.SHIROKO_ARMOR1_2, Talent.SHIROKO_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}
