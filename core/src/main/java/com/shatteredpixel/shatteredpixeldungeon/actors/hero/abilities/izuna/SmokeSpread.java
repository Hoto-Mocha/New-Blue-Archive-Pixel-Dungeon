package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.izuna;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class SmokeSpread extends ArmorAbility {
    {
        baseChargeUse = 25f;
    }

    @Override
    public int icon() {
        return HeroIcon.IZUNA_1;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        hero.busy();
        Sample.INSTANCE.play( Assets.Sounds.GAS );
        int centerVolume = 180+45*hero.pointsInTalent(Talent.IZUNA_ARMOR1_2);
        for (int i : PathFinder.NEIGHBOURS8){
            if (!Dungeon.level.solid[hero.pos+i]){
                GameScene.add( Blob.seed( hero.pos+i, 180+45*hero.pointsInTalent(Talent.IZUNA_ARMOR1_2), SmokeScreen.class ) );
            } else {
                centerVolume += 180+45*hero.pointsInTalent(Talent.IZUNA_ARMOR1_2);
            }
        }

        GameScene.add( Blob.seed( hero.pos, centerVolume, SmokeScreen.class ) );

        if (hero.hasTalent(Talent.IZUNA_ARMOR1_1)) {
            Buff.affect(hero, Haste.class, hero.pointsInTalent(Talent.IZUNA_ARMOR1_1));
            Buff.affect(hero, GreaterHaste.class).set(1+hero.pointsInTalent(Talent.IZUNA_ARMOR1_1));
        }

        if (hero.hasTalent(Talent.IZUNA_ARMOR1_3)) {
            for (int i : PathFinder.NEIGHBOURS8){
                int cell = hero.pos + i;
                Char ch = Actor.findChar(cell);
                if (ch != null && ch.alignment == Char.Alignment.ENEMY && !ch.flying) {
                    Buff.affect(ch, Roots.class, 2*hero.pointsInTalent(Talent.IZUNA_ARMOR1_3));
                    if (Dungeon.level.heroFOV[cell]) {
                        CellEmitter.bottom(cell).start(EarthParticle.FACTORY, 0.05f, 8);
                    }
                }
            }
        }

        Invisibility.dispel();
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(1f);

        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.IZUNA_ARMOR1_1, Talent.IZUNA_ARMOR1_2, Talent.IZUNA_ARMOR1_3, Talent.HEROIC_ENERGY};
    }
}
