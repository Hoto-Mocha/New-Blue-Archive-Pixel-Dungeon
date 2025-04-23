package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class ShootParticle extends SnipeParticle {
    public static Emitter.Factory factory(Char target, int tier, int lvl, Callback callback) {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                if (target == null) return; //타겟이 없으면 아무것도 하지 않음
                ((ShootParticle)emitter.recycle( ShootParticle.class )).reset( x, y, target, tier, lvl, callback );
            }
        };
    }

    public ShootParticle() {
        super();
        color(0x000000);
        am = 0;
    }

    boolean shoot; //총 발사 여부
    Char target = null;
    int tier = 1;
    int lvl = 0;
    Callback callback;
    //총 발사 후 쉬는 구간 이후 마지막 구간 사이는 아무것도 하지 않음

    public void reset( float x, float y, Char target, int tier, int lvl, Callback callback) {
        reset(x, y);

        size(2f);

        shoot = false;

        this.target = target;
        this.tier = tier;
        this.lvl = lvl;
        this.callback = callback;
    }

    @Override
    public void update() {
        super.update();

        if (!shoot && left <= lifespan- FIRST - MIDDLE_REST) { //left가 총 발사 타이밍과 완벽하게 일치하지 않아서 범위로 지정
            Gun gun = Gun.getGun(SR.class, this.tier, this.lvl);
            Gun.Bullet bullet = gun.knockBullet();
            if (Dungeon.hero.hasTalent(Talent.MIYAKO_EX1_2)) {
                bullet.setAccMulti(1f+(2f*Dungeon.hero.pointsInTalent(Talent.MIYAKO_EX1_2)-1f));
            }

            bullet.throwSound();
            bullet.shoot(this.target.pos, false);
            CellEmitter.center(this.target.pos).burst(BlastParticle.FACTORY, 4);
            shoot = true;
        }

        if (left <= 0) {
            this.callback.call();
        }
    }
}
