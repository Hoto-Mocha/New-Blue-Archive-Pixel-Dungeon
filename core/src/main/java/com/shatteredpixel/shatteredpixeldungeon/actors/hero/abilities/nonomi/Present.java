package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.nonomi;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Present extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();

        float duration = 6f + 2f * hero.pointsInTalent(Talent.NONOMI_ARMOR1_1);

        for (Mob mob : Actor.enemiesInHeroFOV(Dungeon.level)) {
            if (hero.hasTalent(Talent.NONOMI_ARMOR1_3) && Random.Float() < 0.02f * hero.pointsInTalent(Talent.NONOMI_ARMOR1_3)) {
                if (!mob.isImmune(ScrollOfSirensSong.Enthralled.class)){
                    AllyBuff.affectAndLoot(mob, hero, ScrollOfSirensSong.Enthralled.class);
                } else {
                    Buff.affect( mob, Charm.class, Charm.DURATION ).object = hero.id();
                }
            }
            Buff.affect(mob, Charm.class, duration).object = hero.id();
            if (hero.hasTalent(Talent.NONOMI_ARMOR1_2)) {
                Buff.affect(mob, Blindness.class, hero.pointsInTalent(Talent.NONOMI_ARMOR1_2));
            }
            mob.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
        }

        hero.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );

        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        Sample.INSTANCE.play(Assets.Sounds.CHARMS);
        hero.spendAndNext(1f);
        hero.sprite.operate(hero.pos);
    }

    @Override
    public int icon() {
        return HeroIcon.NONOMI_1;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.NONOMI_ARMOR1_1, Talent.NONOMI_ARMOR1_2, Talent.NONOMI_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}
