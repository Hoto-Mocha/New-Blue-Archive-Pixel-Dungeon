package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.yuzu;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Cabinet extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.YUZU_2;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        GameScene.flash(0x80FFFFFF);
        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        Buff.affect(hero, TimeStasis.class);
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.YUZU_ARMOR2_1, Talent.YUZU_ARMOR2_2, Talent.YUZU_ARMOR2_3, Talent.HEROIC_ENERGY};
    }

    public static class TimeStasis extends Buff {

        {
            type = buffType.POSITIVE;
            actPriority = BUFF_PRIO-3; //acts after all other buffs, so they are prevented
        }

        @Override
        public boolean attachTo(Char target) {

            if (super.attachTo(target)) {

                Invisibility.dispel();

                //buffs always act last, so the stasis buff should end a turn early.
                spend(20);

                //shouldn't punish the player for going into stasis frequently
                Hunger hunger = Buff.affect(target, Hunger.class);
                if (hunger != null && !hunger.isStarving()) {
                    hunger.satisfy(20);
                }

                target.invisible++;
                target.paralysed++;
                target.next();

                Item.updateQuickslot();

                if (Dungeon.hero != null) {
                    Dungeon.observe();
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean act() {
            detach();
            return true;
        }

        @Override
        public void detach() {
            if (target.invisible > 0) target.invisible--;
            if (target.paralysed > 0) target.paralysed--;
            super.detach();
            Dungeon.observe();
            if (target == Dungeon.hero) {
                Hero hero = (Hero) target;
                hero.yellI("cabinet");
                if (hero.hasTalent(Talent.YUZU_ARMOR2_1)) {
                    hero.heal(3*hero.pointsInTalent(Talent.YUZU_ARMOR2_1));
                    Buff.affect(hero, Hunger.class).satisfy(25*hero.pointsInTalent(Talent.YUZU_ARMOR2_1));
                    if (Random.Float() < 0.25f*hero.pointsInTalent(Talent.YUZU_ARMOR2_1)) {
                        Talent.onFoodEaten(hero, 25*hero.pointsInTalent(Talent.YUZU_ARMOR2_1), null);
                    }
                }
                if (hero.hasTalent(Talent.YUZU_ARMOR2_2) && hero.belongings.weapon() instanceof Gun) {
                    Gun gun = (Gun) hero.belongings.weapon();
                    gun.manualReload(hero.pointsInTalent(Talent.YUZU_ARMOR2_2), true);
                }
                if (hero.hasTalent(Talent.YUZU_ARMOR2_3)) {
                    boolean enemyInFOV = false;
                    for (Char ch : Actor.chars()) {
                        if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.heroFOV[ch.pos]) {
                            enemyInFOV = true;
                            break;
                        }
                    }
                    if (enemyInFOV) {
                        Buff.affect(hero, GreaterHaste.class).set(1+2*hero.pointsInTalent(Talent.YUZU_ARMOR2_3));
                    }
                }
            }
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add( CharSprite.State.PARALYSED );
            else {
                if (target.paralysed == 0) target.sprite.remove( CharSprite.State.PARALYSED );
                if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
            }
        }
    }
}
