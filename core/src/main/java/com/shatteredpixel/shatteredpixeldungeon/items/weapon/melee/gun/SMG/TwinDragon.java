package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SpecialGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Random;

public class TwinDragon extends SMG implements SpecialGun {
    {
        image = ItemSpriteSheet.TWIN_DRAGON;
        tier = 3;
        shotPerShoot = 6;
        shootingAccuracy = 0.5f;
    }

    @Override
    public Bullet knockBullet() {
        return new TwinDragonBullet();
    }

    public class TwinDragonBullet extends SMGBullet {
        @Override
        public void throwSound() {
            for (int i = 0; i < 3; i++) {
                Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, 0.075f*i) {
                    @Override
                    protected void updateValues(float progress) {}

                    @Override
                    protected void onComplete() {
                        Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 0.7f, 0.5f );
                    }
                });
            }
        }
    }
}
