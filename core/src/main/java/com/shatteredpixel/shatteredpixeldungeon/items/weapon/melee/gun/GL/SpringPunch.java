package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

public class SpringPunch extends GL implements SpecialGun {

    {
        image = ItemSpriteSheet.SPRING_PUNCH;
        tier = 3;
        selfHarm = false;
    }

    @Override
    public int baseBulletMax(int lvl) {
        return 4 * (tier()) +
                lvl * (tier());
    }

    @Override
    public Bullet knockBullet(){
        return new FunnyFireworkBullet();
    }

    public class FunnyFireworkBullet extends GLBullet {
        @Override
        public int proc(Char attacker, Char defender, int damage) {
            float dmg = damage;
            if (defender.buff(Paralysis.class) != null) {
                dmg *= 1.5f;
            }
            return super.proc(attacker, defender, (int)Math.floor(dmg));
        }

        @Override
        protected void onThrow(int cell) {
            super.onThrow(cell);

            WandOfBlastWave.BlastWave.blast(cell);
            boolean throwChar = false;
            for (int i : PathFinder.NEIGHBOURS9) {
                if (Actor.findChar(cell+i) != null) {
                    throwChar = true;
                    break;
                }
            }
            if (throwChar) {
                //타격 지점 중앙에 대한 날려 보내기 코드
                Ballistica bulletPath = new Ballistica(curUser.pos, cell, Ballistica.STOP_TARGET);
                Char ch = Actor.findChar(cell);
                Ballistica trajectory;
                int strength;
                if (ch != null) {
                    if ((ch.isAlive() || ch.flying || !Dungeon.level.pit[ch.pos])
                            && bulletPath.path.size() > bulletPath.dist+1 && ch.pos == bulletPath.collisionPos) {
                        strength = buffedLvl() + 3;
                        trajectory = new Ballistica(ch.pos, bulletPath.path.get(bulletPath.dist + 1), Ballistica.MAGIC_BOLT);
                        WandOfBlastWave.throwChar(ch, trajectory, strength, false, true, this);
                    }
                }

                //타격 지점 주변에 대한 날려 보내기 코드
                for (int i : PathFinder.NEIGHBOURS8) {
                    int c = cell+i;
                    ch = Actor.findChar(c);
                    if (ch != null) {
                        if ((ch.isAlive() || ch.flying || !Dungeon.level.pit[ch.pos]) && ch.pos == bulletPath.collisionPos + i) {
                            strength = Math.round(1.5f + buffedLvl() / 2f);
                            trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
                            WandOfBlastWave.throwChar(ch, trajectory, strength, false, true, this);
                        }
                    }
                }
            }
        }
    }

    public static class Recipe extends BaseRecipe {

        @Override
        public Class<? extends Gun> ingredients() {
            return GL.class;
        }

        @Override
        public Class<? extends Gun> result() {
            return SpringPunch.class;
        }
    }
}
