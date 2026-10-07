package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles.MariMiracle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMariMiracles;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class CrossNecklace extends Artifact {

    {
        image = ItemSpriteSheet.CROSS;

        exp = 0;
        levelCap = 10;

        charge = Math.min(level()*2+10, 30);
        partialCharge = 0;
        chargeCap = Math.min(level()*2+10, 30);

        defaultAction = AC_CAST;

        unique = true;
        bones = false;
    }

    public static final String AC_CAST = "CAST";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if ((isEquipped( hero ) || hero.hasTalent(Talent.LIGHT_READING))
                && !cursed
                && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_CAST);
        }
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_CAST)) {

            if (!isEquipped(hero) && !hero.hasTalent(Talent.MARI_T3_2)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            else if (cursed)       GLog.i( Messages.get(this, "cursed") );
            else {

                GameScene.show(new WndMariMiracles(this, hero, false));

            }

        }
    }

    public MariMiracle targetingMiracle = null;

    @Override
    public int targetingPos(Hero user, int dst) {
        if (targetingMiracle == null || targetingMiracle.targetingFlags() == -1) {
            return super.targetingPos(user, dst);
        } else {
            return new Ballistica( user.pos, dst, targetingMiracle.targetingFlags() ).collisionPos;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            if (collect && hero.hasTalent(Talent.MARI_T3_2)){
                activate(hero);
            }

            return true;
        } else
            return false;
    }

    @Override
    public boolean collect( Bag container ) {
        if (super.collect(container)){
            if (container.owner instanceof Hero
                    && passiveBuff == null
                    && ((Hero) container.owner).hasTalent(Talent.MARI_T3_2)){
                activate((Hero) container.owner);
            }
            return true;
        } else{
            return false;
        }
    }

    @Override
    protected void onDetach() {
        if (passiveBuff != null){
            passiveBuff.detach();
            passiveBuff = null;
        }
    }

    public boolean canCast( Hero hero, MariMiracle miracle ){
        return (isEquipped(hero) || (Dungeon.hero.hasTalent(Talent.MARI_T3_2) && hero.belongings.contains(this)))
                && hero.buff(MagicImmune.class) == null
                && charge >= miracle.chargeUse(hero)
                && miracle.canCast(hero);
    }

    public int bulletsCanLoad(int amount) {
        if (charge < amount) {
            return charge;
        } else {
            return amount;
        }
    }

    public void spendCharge( float chargesSpent ){
        partialCharge -= chargesSpent;
        while (partialCharge < 0){
            charge--;
            partialCharge++;
        }

        //target hero level is 1 + 2*tome level
        int lvlDiffFromTarget = Dungeon.hero.lvl - (1+level()*2);
        //plus an extra one for each level after 6
        if (level() >= 7){
            lvlDiffFromTarget -= level()-6;
        }

        if (lvlDiffFromTarget >= 0){
            exp += Math.round(chargesSpent * 10f * Math.pow(1.1f, lvlDiffFromTarget));
        } else {
            exp += Math.round(chargesSpent * 10f * Math.pow(0.75f, -lvlDiffFromTarget));
        }

        if (exp >= (level() + 1) * 180 && level() < levelCap) {
            upgrade();
            Catalog.countUse(HolyTome.class);
            exp -= level() * 180;
            GLog.p(Messages.get(this, "levelup"));
        }

        updateQuickslot();
    }

    public void directCharge(float amount){
        if (charge < chargeCap) {
            partialCharge += amount;
            while (partialCharge >= 1f) {
                charge++;
                partialCharge--;
            }
            if (charge >= chargeCap){
                partialCharge = 0;
                charge = chargeCap;
            }
            updateQuickslot();
        }
        updateQuickslot();
    }

    @Override
    public Item upgrade() {
        chargeCap = Math.min(chargeCap + 2, 30);
        return super.upgrade();
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new CrossRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (cursed || target.buff(MagicImmune.class) != null) return;

        if (charge < chargeCap) {
            if (!isEquipped(target)) amount *= 0.75f*target.pointsInTalent(Talent.MARI_T3_2)/3f;
            partialCharge += 0.25f*amount;
            while (partialCharge >= 1f) {
                charge++;
                partialCharge--;
            }
            if (charge >= chargeCap){
                partialCharge = 0;
                charge = chargeCap;
            }
            updateQuickslot();
        }
    }

    private MariMiracle quickMiracle = null;

    public void setQuickMiracle(MariMiracle miracle){
        if (quickMiracle == miracle){
            quickMiracle = null;
            if (passiveBuff != null){
                ActionIndicator.clearAction((ActionIndicator.Action) passiveBuff);
            }
        } else {
            quickMiracle = miracle;
            if (passiveBuff != null){
                ActionIndicator.setAction((ActionIndicator.Action) passiveBuff);
            }
        }
    }

    private static final String QUICK_CLS = "quick_cls";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (quickMiracle != null) {
            bundle.put(QUICK_CLS, quickMiracle.getClass());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(QUICK_CLS)){
            Class quickCls = bundle.getClass(QUICK_CLS);
            for (MariMiracle miracle : MariMiracle.getAllMiracles()){
                if (miracle.getClass() == quickCls){
                    quickMiracle = miracle;
                }
            }
        }
    }

    public class CrossRecharge extends ArtifactBuff implements ActionIndicator.Action {

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (quickMiracle != null) ActionIndicator.setAction(this);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            super.detach();
            ActionIndicator.clearAction(this);
        }

        @Override
        public boolean act() {
            if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null) {
                if (Regeneration.regenOn()) {
                    float missing = (chargeCap - charge);
                    float turnsToCharge = 35 - missing;
                    if (Dungeon.hero != null && Dungeon.hero.belongings.weapon() instanceof HG) {
                        turnsToCharge *= 0.5f;
                    }
                    turnsToCharge /= RingOfEnergy.artifactChargeMultiplier(target);
                    turnsToCharge = Math.max(1, turnsToCharge); //충전에 반드시 1턴은 필요함
                    float chargeToGain = (1f / turnsToCharge);
                    if (!isEquipped(Dungeon.hero)){
                        chargeToGain *= 0.75f*Dungeon.hero.pointsInTalent(Talent.MARI_T3_2)/3f;
                    }
                    partialCharge += chargeToGain;
                }

                while (partialCharge >= 1) {
                    charge++;
                    partialCharge -= 1;
                    if (charge == chargeCap){
                        partialCharge = 0;
                    }

                }
            } else {
                partialCharge = 0;
            }

            updateQuickslot();

            spend( TICK );

            return true;
        }

        @Override
        public String actionName() {
            return quickMiracle.name();
        }

        @Override
        public int actionIcon() {
            return quickMiracle.icon() + HeroIcon.SPELL_ACTION_OFFSET;
        }

        @Override
        public int indicatorColor() {
//            if (quickMiracle == GuidingLight.INSTANCE && quickMiracle.chargeUse(Dungeon.hero) == 0){
//                return 0x0063ff;
//            } else
            {
                return 0x002157;
            }
        }

        @Override
        public void doAction() {
            if (cursed){
                GLog.w(Messages.get(CrossNecklace.this, "cursed"));
                return;
            }

            if (!canCast(Dungeon.hero, quickMiracle)){
                GLog.w(Messages.get(CrossNecklace.this, "no_spell"));
                return;
            }

            if (QuickSlotButton.targetingSlot != -1 &&
                    Dungeon.quickslot.getItem(QuickSlotButton.targetingSlot) == CrossNecklace.this) {
                targetingMiracle = quickMiracle;
                int cell = QuickSlotButton.autoAim(QuickSlotButton.lastTarget, CrossNecklace.this);

                if (cell != -1){
                    GameScene.handleCell(cell);
                } else {
                    //couldn't auto-aim, just target the position and hope for the best.
                    GameScene.handleCell( QuickSlotButton.lastTarget.pos );
                }
            } else {
                quickMiracle.onCast(CrossNecklace.this, Dungeon.hero);

                if (quickMiracle.targetingFlags() != -1 && Dungeon.quickslot.contains(CrossNecklace.this)){
                    targetingMiracle = quickMiracle;
                    QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(CrossNecklace.this));
                }
            }
        }
    }

}
