package com.shatteredpixel.shatteredpixeldungeon.actors.hero.miracles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CrossNecklace;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class UnholyDetection extends InventoryMariMiracle {
    public static final UnholyDetection INSTANCE = new UnholyDetection();

    @Override
    public int icon() {
        return HeroIcon.UNHOLY_DETECTION;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return (item instanceof EquipableItem || item instanceof Wand) && !item.isIdentified() && !item.cursedKnown;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 7-hero.pointsInTalent(Talent.MARI_T1_2);
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MARI_T1_2);
    }

    @Override
    protected void onItemSelected(CrossNecklace cross, Hero hero, Item item) {
        if (item == null){
            return;
        }

        item.cursedKnown = true;

        if (item.cursed){
            hero.yellW("cursed");
        } else {
            hero.yellI("uncursed");
        }

        hero.spend( 1f );
        hero.busy();
        hero.sprite.operate(hero.pos);
        hero.sprite.parent.add( new Identification( hero.sprite.center().offset( 0, -16 ) ) );

        Sample.INSTANCE.play( Assets.Sounds.READ );
        onMiracleCast(cross, hero);
    }

}
