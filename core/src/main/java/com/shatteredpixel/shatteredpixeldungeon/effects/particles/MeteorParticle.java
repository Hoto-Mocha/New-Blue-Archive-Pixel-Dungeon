package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class MeteorParticle extends PixelParticle {
    public static Emitter.Factory factory(Callback callback, int size) {
        return new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((MeteorParticle)emitter.recycle( MeteorParticle.class )).reset( x, y, callback, size );
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
    }

    public MeteorParticle() {
        super();
        color(0xFF0000);
    }


    PointF startPoint;
    PointF endPoint;
    PointF delta;
    int size;

    boolean shoot; //폭격 여부
    Callback callback = null;

    public void reset(float x, float y, Callback callback, int size) {
        revive();

        this.size = size;
        float speedMulti = 288f/(13f*size+70f); //2~50 범위에서 3~0.4의 값을 가짐
        endPoint = new PointF(x, y);
        startPoint = new PointF(x-2*(10+size), y-4*(10+size));
        delta = new PointF(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        this.x = startPoint.x;
        this.y = startPoint.y;

        this.shoot = false;
        this.callback = callback;

        left = lifespan = 1/speedMulti;

        //등속도 운동 공식
        float speedX = (delta.x)*speedMulti;
        float speedY = (delta.y)*speedMulti;

        float accX = 0;
        float accY = 0;

        acc.set( accX, accY );
        speed.set( speedX, speedY );
    }

    @Override
    public void update() {
        super.update();
        am = Math.min(1, ((lifespan-left)*5)/(lifespan)); //0~(lifespan/5)초에서 투명도가 0~1이 되도록 조절. 나머지는 항상 1
        float firstSize = size;
        float sizeAdd = 4;
        size(firstSize+sizeAdd*(1-(left/lifespan))); //처음에는 firstSize이었다가 끝에는 firstSize+sizeAdd가 됨

        if (!shoot && left <= 0) {
            shoot = true;

            this.callback.call();
        }
    }
}
