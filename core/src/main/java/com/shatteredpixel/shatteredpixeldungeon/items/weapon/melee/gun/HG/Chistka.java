package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Chistka extends HG implements SpecialGun {

    {
        image = ItemSpriteSheet.CHISTKA;
        tier = 3;
    }

    public float purgeChance(int lvl) {
        return (2f+lvl)/100f;
    }

    public boolean purge(Hero hero, int cell) {
        Char ch = Actor.findChar(cell);
        if (Random.Float() >= purgeChance(buffedLvl())) return false;
        if (ch == null) return false;
        if (ch instanceof NPC) return false;
        if (ch.alignment != Char.Alignment.ENEMY) return false;
        if (Char.hasProp(ch, Char.Property.MINIBOSS) || Char.hasProp(ch, Char.Property.BOSS)) {
            return false;
        }

        hero.sprite.showStatus(CharSprite.WARNING, Messages.get(this, "purge"));
        hero.sprite.zap(cell);
        hero.spendAndNext(knockBullet().delayFactor(hero));

        if (ch instanceof Mob){
            ((Mob) ch).EXP = 0;
            ((Mob) ch).rollToDropLoot();
        }
        if (Dungeon.level.heroFOV[ch.pos]) {
            CellEmitter.get( ch.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
            Sample.INSTANCE.play( Assets.Sounds.PUFF );
        }
        ch.HP = 0;
        if (ch.isAlive()) {
            if (ch.buff(Brute.BruteRage.class) != null){
                ch.buff(Brute.BruteRage.class).detach();
            }
        }
        ch.destroy();

        ch.sprite.killAndErase();
        Dungeon.level.mobs.remove(ch);

        hero.checkVisibleMobs();
        AttackIndicator.updateState();

        return true;
    }

    @Override
    public Bullet knockBullet() {
        return new ChistkaBullet();
    }

    public class ChistkaBullet extends Bullet {
        @Override
        public void cast(Hero user, int dst) {
            if (purge(user, dst)) return;
            super.cast(user, dst);
        }
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return HG.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return Chistka.class;
        }
    }
}
