package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mari;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class MariTrinity extends ArmorAbility {
    {
        baseChargeUse = 35f;
    }

    @Override
    public int icon() {
        return HeroIcon.MARI_2;
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return super.targetedPos(user, dst);
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);

        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(TrinityBuff.class) != null) {
            hero.yellW("already_casting");
            return;
        }

        hero.busy();
        Buff.affect(hero, TrinityBuff.class).reset();
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        hero.spendAndNext(Actor.TICK);

        armor.charge -= chargeUse(hero);
        Item.updateQuickslot();
        Invisibility.dispel();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.MARI_ARMOR2_1, Talent.MARI_ARMOR2_2, Talent.MARI_ARMOR2_3, Talent.HEROIC_ENERGY};
    }

    public static class TrinityBuff extends ShieldBuff {
        {
            type = buffType.POSITIVE;

            detachesAtZero = false;
            shieldUsePriority = 1;
        }

        public static final float DURATION = 15f;

        private boolean red = false;
        private boolean yellow = false;
        private boolean blue = false;

        private int mindVision = 0;
        private int defense = 0;
        public int left = 15;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            if (!yellow && !red && !blue)   return BuffIndicator.TRINITY_NONE;
            if (!yellow && red  && !blue)    return BuffIndicator.TRINITY_RED;
            if (yellow  && !red && !blue)    return BuffIndicator.TRINITY_YELLOW;
            if (!yellow && !red && blue )    return BuffIndicator.TRINITY_BLUE;
            if (yellow  && red  && !blue)     return BuffIndicator.TRINITY_YELLOW_RED;
            if (yellow  && !red && blue )     return BuffIndicator.TRINITY_YELLOW_BLUE;
            if (!yellow && red  && blue )     return BuffIndicator.TRINITY_RED_BLUE;
            else                            return BuffIndicator.TRINITY_ALL;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - left) / DURATION);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(left);
        }

        @Override
        public boolean act() {
            left--;
            if (left <= 0){
                detach();
                return true;
            }

            spend(TICK);
            return true;
        }

        public void reset() {
            red = false;
            yellow = false;
            blue = false;
            mindVision = 0;
            defense = 0;
            left = (int)DURATION+1;
        }

        public void extend(){
            left += 5;
            BuffIndicator.refreshHero();
        }

        public void mindVisionCast(Hero hero) {
            yellow = true;
            mindVision++;
            Dungeon.observe();
            extend();
        }

        public void defenseCast() {
            red = true;
            defense++;
            if (defense > 2) {
                defense = 2;
            }
            extend();
        }

        public void shieldCast(Hero hero) {
            blue = true;
            int amount = 10*hero.pointsInTalent(Talent.MARI_ARMOR2_3);
            if (shielding() + amount > hero.HT) {
                hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.HT-shielding()), FloatingText.SHIELDING);
                setShield(hero.HT);
            } else {
                hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(amount), FloatingText.SHIELDING);
                incShield(amount);
            }
            extend();
        }

        public int mindVisionRange() {
            return Math.min(12, mindVision*Dungeon.hero.pointsInTalent(Talent.MARI_ARMOR2_2));
        }

        private int minDr(Hero hero) {
            return defense*hero.pointsInTalent(Talent.MARI_ARMOR2_1);
        }

        private int maxDr(Hero hero) {
            return defense*5*hero.pointsInTalent(Talent.MARI_ARMOR2_1);
        }

        public int drRoll(Hero hero) {
            return Random.NormalIntRange(minDr(hero), maxDr(hero));
        }

        public boolean isTrinityComplete() {
            return red && yellow && blue;
        }

        @Override
        public String desc() {
            String desc = Messages.get(this, "desc") + "\n\n";

            if (red)                    desc += Messages.get(this, "desc_red", minDr(Dungeon.hero), maxDr(Dungeon.hero)) + "\n";
            if (yellow)                 desc += Messages.get(this, "desc_yellow", mindVisionRange()) + "\n";
            if (blue)                   desc += Messages.get(this, "desc_blue", shielding()) + "\n";
            if (red && yellow && blue)  desc += Messages.get(this, "desc_all") + "\n";
            if (red || yellow || blue) desc += "\n";
            desc += Messages.get(this, "desc_left", left);
            return desc;
        }

        private static final String RED = "red";
        private static final String YELLOW = "yellow";
        private static final String BLUE = "blue";
        private static final String MIND_VISION = "mind_vision";
        private static final String DEFENSE = "defense";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(RED, red);
            bundle.put(YELLOW, yellow);
            bundle.put(BLUE, blue);
            bundle.put(MIND_VISION, mindVision);
            bundle.put(DEFENSE, defense);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            red = bundle.getBoolean(RED);
            yellow = bundle.getBoolean(YELLOW);
            blue = bundle.getBoolean(BLUE);
            mindVision = bundle.getInt(MIND_VISION);
            defense = bundle.getInt(DEFENSE);
            left = bundle.getInt(LEFT);
        }
    }
}
