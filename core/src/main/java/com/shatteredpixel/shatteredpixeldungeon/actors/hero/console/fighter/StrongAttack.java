package com.shatteredpixel.shatteredpixeldungeon.actors.hero.console.fighter;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StrongAttack extends FighterConsoleContent {
    public static final StrongAttack INSTANCE = new StrongAttack();

    @Override
    public int icon() {
        return HeroIcon.FIGHTER_STROING_ATK;
    }

    @Override
    public boolean execute(Hero hero, int target) {
        if (!super.execute(hero, target)) return false;

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
        int damage = Math.round(damageRoll(hero)*(isEnhanced(hero) ? 1.5f : 1) - enemy.drRoll());
        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                enemy.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                enemy.damage(damage, hero);
                if (isEnhanced(hero)) {
                    Elastic.pushEnemy(hero, enemy, hero.belongings.weapon(), 3);
                }
                hero.spendAndNext(1f);
            }
        });

        return true;
    }

    @Override
    public boolean isEnhanced(Hero hero) {
        return super.isEnhanced(hero) || ((hero.buff(FighterConsoleBuff.class) != null && hero.buff(FighterConsoleBuff.class).isAttackEnhanced()));
    }
}
