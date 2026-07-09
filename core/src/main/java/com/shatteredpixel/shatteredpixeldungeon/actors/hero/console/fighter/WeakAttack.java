package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WeakAttack extends FighterConsoleContent {
    public static final WeakAttack INSTANCE = new WeakAttack();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_WEAK_ATK;
    }

    @Override
    public boolean execute(Hero hero) {
        if (!super.execute(hero)) return false;

        ArrayList<Mob> adjacentMobs = new ArrayList<>();
        for (Char ch : Actor.chars()) {
            if (ch.alignment == Char.Alignment.ENEMY && ch instanceof Mob && Dungeon.level.adjacent(hero.pos, ch.pos)) {
                adjacentMobs.add((Mob) ch);
            }
        }
        if (adjacentMobs.isEmpty()) return false;

        Mob enemy = Random.element(adjacentMobs);
        if (enemy == null || enemy.alignment != Char.Alignment.ENEMY) return false;

        hero.busy();
        Buff.affect(hero, FighterConsoleBuff.class).attackEnhance();
        int damage = damageRoll(hero)/(isEnhanced(hero) ? 1 : 2) - enemy.drRoll();
        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                enemy.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.HIT);
                enemy.damage(damage, hero);
                hero.spendAndNext(0.5f);
            }
        });

        return true;
    }

    @Override
    public boolean isEnhanced(Hero hero) {
        return super.isEnhanced(hero) || ((hero.buff(FighterConsoleBuff.class) != null && hero.buff(FighterConsoleBuff.class).isAttackEnhanced()));
    }
}
