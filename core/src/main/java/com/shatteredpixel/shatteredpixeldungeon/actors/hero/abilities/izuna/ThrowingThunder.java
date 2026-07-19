package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.izuna;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fantasy.Thunder;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ThrowingThunder extends ArmorAbility {
    {
        baseChargeUse = 50f;
    }

    @Override
    public int icon() {
        return HeroIcon.IZUNA_3;
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return super.targetedPos(user, dst);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) return;
        Ballistica aim = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
        if (aim.collisionPos == hero.pos || target == hero.pos) {
            hero.yellW("cannot_self");
            return;
        }

        new PotOThunder().cast(hero, target);

        armor.charge -= chargeUse( hero );
        armor.updateQuickslot();
        Invisibility.dispel();
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.IZUNA_ARMOR3_1, Talent.IZUNA_ARMOR3_2, Talent.IZUNA_ARMOR3_3, Talent.HEROIC_ENERGY};
    }

    public static class PotOThunder extends Item {
        {
            image = ItemSpriteSheet.THUNDERBOLT;
        }

        @Override
        protected void onThrow(int cell) {
            Char enemy = Actor.findChar(cell);
            if (enemy == null || enemy.alignment != Char.Alignment.ENEMY) return;
            int min = 20+5*Dungeon.hero.pointsInTalent(Talent.IZUNA_ARMOR3_2);
            int max = 60+15*Dungeon.hero.pointsInTalent(Talent.IZUNA_ARMOR3_2);
            int damage = Hero.heroDamageIntRange(min, max);

            enemy.damage(damage, new Electricity());
            if (Random.Float() < 0.25f*Dungeon.hero.pointsInTalent(Talent.IZUNA_ARMOR3_3)) {
                Buff.affect(enemy, Paralysis.class, 10f);
            } else {
                Buff.affect(enemy, Cripple.class, 10f);
            }

            if (Dungeon.hero.hasTalent(Talent.IZUNA_ARMOR3_1)) {
                ArrayList<Char> affected = new ArrayList<>();
                ArrayList<Lightning.Arc> arcs = new ArrayList<>();
                Shocking.arc( Dungeon.hero, enemy, Dungeon.level.water[enemy.pos] ? 2 : 1, affected, arcs );

                affected.remove(enemy); //defender isn't hurt by lightning
                for (Char ch : affected) {
                    if (ch.alignment != Dungeon.hero.alignment) {
                        ch.damage(Math.round(damage * 0.2f * Dungeon.hero.pointsInTalent(Talent.IZUNA_ARMOR3_1)), new Electricity());
                    }
                }
            }

            Thunder.thunderEffect(enemy.sprite);
            Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);

            CharSprite s = enemy.sprite;
            if (s != null && s.parent != null) {
                ArrayList<Lightning.Arc> arcs = new ArrayList<>();
                arcs.add(new Lightning.Arc(new PointF(s.x, s.y + s.height / 2), new PointF(s.x + s.width, s.y + s.height / 2)));
                arcs.add(new Lightning.Arc(new PointF(s.x + s.width / 2, s.y), new PointF(s.x + s.width / 2, s.y + s.height)));
                s.parent.add(new Lightning(arcs, null));
            }
        }
    }
}
