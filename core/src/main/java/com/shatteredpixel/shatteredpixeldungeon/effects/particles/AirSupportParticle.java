package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public class AirSupportParticle extends PixelParticle {
    public static Emitter.Factory factory(int target, Callback callback) {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((AirSupportParticle)emitter.recycle( AirSupportParticle.class )).reset( x, y, target, callback );
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
    }

    public AirSupportParticle() {
        super();
        color(0xFF0000);
    }

    protected final float SPEED_MULTI = 3f;

    PointF startPoint;
    PointF endPoint;
    PointF delta;

    boolean shoot; //폭격 여부
    Callback callback = null;
    int target = -1;

    public void reset(float x, float y, int target, Callback callback) {
        revive();

        endPoint = new PointF(x, y);
        startPoint = new PointF(x-40, y-80);
        delta = new PointF(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        this.x = startPoint.x;
        this.y = startPoint.y;

        this.shoot = false;
        this.callback = callback;
        this.target = target;

        left = lifespan = 1/ SPEED_MULTI;

        //종시에 초기 속도의 3배가 되는 공식
//        float speedX = (delta.x)/2f*SPEED_MULTI;
//        float speedY = (delta.y)/2f*SPEED_MULTI;
//
//        float accX = (delta.x)*SPEED_MULTI*SPEED_MULTI;
//        float accY = (delta.y)*SPEED_MULTI*SPEED_MULTI;

        //등속도 운동 공식
        float speedX = (delta.x)*SPEED_MULTI;
        float speedY = (delta.y)*SPEED_MULTI;

        float accX = 0;
        float accY = 0;

        acc.set( accX, accY );
        speed.set( speedX, speedY );
    }

    @Override
    public void update() {
        super.update();
        am = Math.min(1, ((lifespan-left)*5)/(lifespan)); //0~(lifespan/5)초에서 투명도가 0~1이 되도록 조절. 나머지는 항상 1
        float firstSize = 2;
        float sizeAdd = 4f;
        size(firstSize*(1+sizeAdd*(1-(left/lifespan)))); //처음에는 firstSize이었다가 끝에는 firstSize+sizeAdd가 됨

        if (!shoot && left <= 0) {
            //2~(현재 계층)~5의 티어를 가지고, 보스 층에서 다음 티어를 가지는 것을 방지하기 위해 1을 뺌. 강화 수치는 영웅 레벨을 5로 나누어 소수점을 버린 값에 3을 더한 값을 취함.
            Gun gun = Gun.getGun(GL.class, (int)GameMath.gate(2, 1+(Dungeon.scalingDepth()-1)/5f, 5), Dungeon.hero.lvl/5+3);
            Gun.Bullet bullet = gun.knockBullet();

            bullet.shoot(this.target, false);
            CellEmitter.center(this.target).burst(BlastParticle.FACTORY, 4);
            shoot = true;

            this.callback.call();
        }
    }
}
