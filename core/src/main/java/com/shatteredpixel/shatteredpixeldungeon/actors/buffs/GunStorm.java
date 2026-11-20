package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.AirSupportParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BulletParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG.MG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MT.MT;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG.SG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG.SMG;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.FlashShield;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class GunStorm extends Buff implements ActionIndicator.Action {

    public enum AbilityType {
        DUEL_AR,
        DUEL_GL,
        DUEL_HG,
        DUEL_MG,
        DUEL_SG,
        DUEL_SMG,
        DUEL_SR,
        NONE,
        SINGLE_GUN,
        DOUBLE_GUN,
        GUN_N_SHIELD
    }

    private AbilityType abilityType = AbilityType.NONE;

    public AbilityType checkWeapon(Hero hero) {
        if (hero == null || hero.belongings == null) {
            abilityType = AbilityType.NONE;
            return abilityType;
        }

        KindOfWeapon w1 = hero.belongings.weapon;
        KindOfWeapon w2 = hero.belongings.secondWep;

        boolean w1IsGun = w1 instanceof Gun;
        boolean w2IsGun = w2 instanceof Gun;
        boolean w1IsShield = w1 instanceof FlashShield;
        boolean w2IsShield = w2 instanceof FlashShield;

        if (w1 instanceof AR && w2 instanceof AR) {
            abilityType = AbilityType.DUEL_AR;
        } else if (w1 instanceof GL && w2 instanceof GL) {
            abilityType = AbilityType.DUEL_GL;
        } else if (w1 instanceof HG && w2 instanceof HG) {
            abilityType = AbilityType.DUEL_HG;
        } else if (w1 instanceof MG && w2 instanceof MG) {
            abilityType = AbilityType.DUEL_MG;
        } else if (w1 instanceof SG && w2 instanceof SG) {
            abilityType = AbilityType.DUEL_SG;
        } else if (w1 instanceof SMG && w2 instanceof SMG) {
            abilityType = AbilityType.DUEL_SMG;
        } else if (w1 instanceof SR && w2 instanceof SR) {
            abilityType = AbilityType.DUEL_SR;
        } else if (w1IsGun && w2IsGun) {
            abilityType = AbilityType.DOUBLE_GUN;
        } else if ((w1IsGun && w2IsShield) || (w1IsShield && w2IsGun)) {
            abilityType = AbilityType.GUN_N_SHIELD;
        } else if (w1IsGun || w2IsGun) {
            abilityType = AbilityType.SINGLE_GUN;
        } else {
            abilityType = AbilityType.NONE;
        }

        ActionIndicator.refresh();

        if (abilityType == AbilityType.NONE) detach();

        return abilityType;
    }

    public static boolean checkAvailableGuns(Hero hero) {
        KindOfWeapon w1 = hero.belongings.weapon;
        KindOfWeapon w2 = hero.belongings.secondWep;

        boolean w1Available = w1 instanceof Gun && ((Gun) w1).round() > 0;
        boolean w2Available = w2 instanceof Gun && ((Gun) w2).round() > 0;

        return w1Available || w2Available;
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    public AbilityType abilityType() {
        return abilityType;
    }

    @Override
    public boolean act() {
        if (!(target instanceof Hero)) detach();
        if (checkWeapon((Hero)target) == AbilityType.NONE) detach();
        return super.act();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name_"+abilityType().name());
    }

    @Override
    public Visual primaryVisual() {
        Hero hero = (Hero)target;
        Image icon;
        switch (abilityType()) {
            case DUEL_AR:
                icon = new ItemSprite(ItemSpriteSheet.AR_PLACEHOLDER);
                icon.color(0xFF0000);
                return icon;
            case DUEL_GL:
                icon = new ItemSprite(ItemSpriteSheet.GL_PLACEHOLDER);
                icon.color(0xFF8000);
                return icon;
            case DUEL_HG:
                icon = new ItemSprite(ItemSpriteSheet.HG_PLACEHOLDER);
                icon.color(0xFFFF00);
                return icon;
            case DUEL_MG:
                icon = new ItemSprite(ItemSpriteSheet.MG_PLACEHOLDER);
                icon.color(0x00FF00);
                return icon;
            case DUEL_SG:
                icon = new ItemSprite(ItemSpriteSheet.SG_PLACEHOLDER);
                icon.color(0x0080FF);
                return icon;
            case DUEL_SMG:
                icon = new ItemSprite(ItemSpriteSheet.SMG_PLACEHOLDER);
                icon.color(0x0000FF);
                return icon;
            case DUEL_SR:
                icon = new ItemSprite(ItemSpriteSheet.SR_PLACEHOLDER);
                icon.color(0xFF00FF);
                return icon;
            case DOUBLE_GUN:
                return new ItemSprite(hero.belongings.weapon);
            case GUN_N_SHIELD:
                return new ItemSprite(new FlashShield());
            case SINGLE_GUN:
                if (hero.belongings.weapon instanceof Gun) return new ItemSprite(hero.belongings.weapon);
                else if (hero.belongings.secondWep instanceof Gun) return new ItemSprite(hero.belongings.secondWep);
                else return new HeroIcon(this);
            case NONE: default:
                return new HeroIcon(this);
        }
    }

    @Override
    public Visual secondaryVisual() {
        Image ico;
        Hero hero = (Hero)target;
        if (hero.belongings.weapon == null || hero.belongings.secondWep == null) return null;
        switch (abilityType()) {
            case DUEL_AR:
            case DUEL_GL:
            case DUEL_HG:
            case DUEL_MG:
            case DUEL_SG:
            case DUEL_SMG:
            case DUEL_SR:
                return null;
            case DOUBLE_GUN:
                ico = new ItemSprite(hero.belongings.secondWep);
                break;
            case GUN_N_SHIELD:
                if (hero.belongings.weapon instanceof Gun) ico = new ItemSprite(hero.belongings.weapon);
                else ico = new ItemSprite(hero.belongings.secondWep);
                break;
            case SINGLE_GUN:
                if (hero.belongings.weapon instanceof Gun) ico = new ItemSprite(hero.belongings.weapon);
                else if (hero.belongings.secondWep instanceof Gun) ico = new ItemSprite(hero.belongings.secondWep);
                else ico = null;
                break;
            case NONE: default:
                ico = null;
        }
        if (ico == null) return null;
        ico.scale.set(PixelScene.align(0.65f));
        ico.brightness(1f);
        return ico;
    }

    @Override
    public int indicatorColor() {
        switch (abilityType()) {
            case DUEL_AR:
            case DUEL_GL:
            case DUEL_HG:
            case DUEL_MG:
            case DUEL_SG:
            case DUEL_SMG:
            case DUEL_SR:
                return 0x161616;
            default: return 0xFDE1AA;
        }
    }

    @Override
    public void doAction() {
        if (abilityType() == AbilityType.NONE) return;
        if (!checkAvailableGuns((Hero)target)) { //쏠 수 있는 총이 하나도 없으면
            reloadAllGuns((Hero)target, ((Hero) target).belongings.weapon, ((Hero) target).belongings.secondWep);
            return;
        }

        if (abilityType() == AbilityType.DUEL_HG){
            KindOfWeapon w1 = hero.belongings.weapon;
            KindOfWeapon w2 = hero.belongings.secondWep;
            showTime(hero, (Gun)w1, (Gun)w2);
            return;
        }

        GameScene.selectCell(shooter);
    }

    private static CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer cell ) {
            if (cell == null) return;

            Hero hero = Dungeon.hero;

            KindOfWeapon w1 = hero.belongings.weapon;
            KindOfWeapon w2 = hero.belongings.secondWep;
            AbilityType abilityType = hero.buff(GunStorm.class).abilityType();

            if (cell == hero.pos) {
                reloadAllGuns(hero, w1, w2);
                return;
            }

            switch (abilityType) {
                case DOUBLE_GUN:
                    shootDifferentGuns(hero, (Gun)w1, (Gun)w2, cell);
                    break;
                case GUN_N_SHIELD:
                    if (w1 instanceof Gun) coverFire(hero, (Gun)w1, cell);
                    else if (w2 instanceof Gun) coverFire(hero, (Gun)w2, cell);
                    break;
                case DUEL_AR:
                    onePointShot(hero, (Gun)w1, (Gun)w2, cell);
                    break;
                case DUEL_GL:
                    saturationBombing(hero, (Gun)w1, (Gun)w2, cell);
                    break;
                case DUEL_HG:
                    //doAction()에서 구현함
                    break;
            }

        }

        @Override
        public String prompt() {
            return Messages.get(GunStorm.class, "prompt");
        }
    };

    private static final String ABILITY_TYPE = "ability_type";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ABILITY_TYPE, abilityType);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        abilityType = bundle.getEnum(ABILITY_TYPE, AbilityType.class);
        ActionIndicator.setAction(this);
    }

    //양손 총기 재장전
    public static void reloadAllGuns(Hero hero, KindOfWeapon w1, KindOfWeapon w2) {
        Gun gun1;
        Gun gun2;
        if (w1 instanceof Gun) {
            gun1 = (Gun)w1;
        } else {
            gun1 = null;
        }
        if (w2 instanceof Gun) {
            gun2 = (Gun)w2;
        } else {
            gun2 = null;
        }
        if (gun1 == null && gun2 == null) {
            return;
        }
        float reloadTurns = 0;
        if (gun1 != null) {
            gun1.quickReload();
            reloadTurns += gun1.reloadTime();
        }
        if (gun2 != null) {
            gun2.quickReload();
            reloadTurns += gun2.reloadTime();
        }
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        hero.spendAndNext(reloadTurns);
    }

    //서로 다른 총
    public static void shootDifferentGuns(Hero hero, Gun gun1, Gun gun2, int cell) {
        if (gun1.round() <= 0 && gun2.round() <= 0) {
            reloadAllGuns(hero, gun1, gun2);
            return;
        }

        if (gun1.round() <= 0) {
            gun2.shoot(cell);
            return;
        } else if (gun2.round() <= 0) {
            gun1.shoot(cell);
            return;
        }

        float delay = Math.max(gun1.delayFactor(hero), gun2.delayFactor(hero));
        gun1.burstShoot(cell, 0);
        hero.sprite.parent.add(new Tweener(hero.sprite.parent, 0.1f) {
            @Override
            protected void updateValues(float progress) {}

            @Override
            protected void onComplete() {
                gun2.shoot(cell, delay);
                super.onComplete();
            }
        });
    }

    //방패와 총
    public static void coverFire(Hero hero, Gun gun, int cell) {
        hero.busy();
        if (gun.round() <= 0) {
            reloadAllGuns(hero, gun, null);
            return;
        }
        float delay = 2 * gun.knockBullet().delayFactor(hero);
        gun.shoot(cell, delay);
        System.out.println(delay);
        Buff.affect(hero, CoverFireBuff.class, delay);
    }

    public static class CoverFireBuff extends FlavourBuff {
        public float damageResistance() {
            return 0.25f;
        }
    }

    //더블 AR
    public static void onePointShot(Hero hero, Gun gun1, Gun gun2, int cell) {
        if (gun1.round() <= 0 || gun2.round() <= 0) {
            reloadAllGuns(hero, gun1, gun2);
            return;
        }

        float delay = Math.max(gun1.knockBullet().delayFactor(hero), gun2.knockBullet().delayFactor(hero));
        gun1.burstShoot(cell, 0, 1f, false, true, new Gun.HitProc() {
            @Override
            public int proc(Char attacker, Char defender, int damage) {
                Buff.affect(defender, Vulnerable.class, delay);
                return damage;
            }
        });
        hero.sprite.parent.add(new Tweener(hero.sprite.parent, 0.1f) {
            @Override
            protected void updateValues(float progress) {}

            @Override
            protected void onComplete() {
                gun2.shoot(cell, delay);
                super.onComplete();
            }
        });
    }

    //더블 GL
    public static void saturationBombing(Hero hero, Gun gun1, Gun gun2, int cell) {
        hero.busy();
        hero.sprite.zap(cell);

        float tweenerDelay = 0; //이펙트 딜레이
        final int rounds = gun1.round()+gun2.round(); //초기 탄창 수
        final Gun.Bullet bullet1 = gun1.knockBullet(); //발사음 재생을 위한 인스턴스
        final Gun.Bullet bullet2 = gun2.knockBullet(); //발사음 재생을 위한 인스턴스
        for (int i = 0; i < rounds; i++) {
            hero.sprite.parent.add(new Tweener(hero.sprite.parent, tweenerDelay) {
                @Override
                protected void updateValues(float progress) {
                    hero.busy();
                }

                @Override
                protected void onComplete() {
                    super.onComplete();
                    if (rounds % 2 == 0) {
                        bullet1.throwSound(); //발사음 재생
                    } else {
                        bullet2.throwSound(); //발사음 재생
                    }
                    //탄환 파티클을 위쪽 방향으로 생성. 기본 3개, 발사 개수가 증가하면 발사 수당 3개
                    CellEmitter.heroCenter(hero.pos).burst(BulletParticle.factory(new PointF(DungeonTilemap.tileCenterToWorld(hero.pos).x, DungeonTilemap.tileCenterToWorld(hero.pos).y-40)), 3);
                    //연기 파티클 생성
                    CellEmitter.get(hero.pos).burst(SmokeParticle.FACTORY, 1);
                }
            });
            //장탄수가 얼마나 되든 총 0.2초가 소모됨. 장탄수가 많을수록 발사 속도가 빠르고, 적을수록 발사 속도가 느려짐.
            tweenerDelay += 0.4f/rounds;
        }


        tweenerDelay = 0.6f; //기본 지연 시간 0.6초
        float delay = Math.max(gun1.knockBullet().delayFactor(hero), gun2.knockBullet().delayFactor(hero));
        int blastAmount = gun1.round()+gun2.round();
        boolean isPrimaryGun = gun1.round() >= gun2.round();

        while (blastAmount > 0) {
            int finalCell = cell + PathFinder.NEIGHBOURS9[Random.Int(9)];

            blastAmount--;

            final int finalBlastAmount = blastAmount;
            final float finalDelay = tweenerDelay;
            boolean finalIsPrimaryGun = isPrimaryGun;
            Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, finalDelay) {
                @Override
                protected void updateValues(float progress) {}

                @Override
                protected void onComplete() {
                    super.onComplete();
                    CellEmitter.heroCenter(finalCell).burst(AirSupportParticle.factory(new Callback() {
                        @Override
                        public void call() {
                            if (finalIsPrimaryGun) {
                                gun1.shootWithoutBullet(finalCell, 1f, false, true, true, null);
                                gun1.useRound();
                            } else {
                                gun2.shootWithoutBullet(finalCell, 1f, false, true, true, null);
                                gun2.useRound();
                            }
                            CellEmitter.center(finalCell).burst(BlastParticle.FACTORY, 4);

                            if (finalBlastAmount <= 0) {
                                Dungeon.hero.spendAndNext(delay);
                            }
                        }
                    }), 1);
                }
            });
            tweenerDelay += 0.1f;
            isPrimaryGun = !isPrimaryGun;
        }
    }

    //더블 HG
    public static void showTime(Hero hero, Gun gun1, Gun gun2) {
        hero.busy();
        int index = Random.Int(8);
        int cell = hero.pos+PathFinder.NEIGHBOURS8[index];
        int oppositeCell = hero.pos+PathFinder.NEIGHBOURS8[7-index];

        hero.sprite.zap(cell);
        int distance = 1;
        int degree = 90;

        //정면 범위 타격
        Ballistica aim = new Ballistica(hero.pos, cell, Ballistica.WONT_STOP);
        ConeAOE aoe = new ConeAOE(aim, distance, degree, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);
        for (int c : aoe.cells) {
            CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
            gun1.shootWithoutBullet(c, 2f, false, false, false, new Gun.HitProc() {
                @Override
                public int proc(Char attacker, Char defender, int damage) {
                    return damage/2;
                }
            });
        }

        //후면 범위 타격
        aim = new Ballistica(hero.pos, oppositeCell, Ballistica.WONT_STOP);
        aoe = new ConeAOE(aim, distance, degree, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);
        for (int c : aoe.cells) {
            CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
            gun2.shootWithoutBullet(c, 2f, false, false, false, new Gun.HitProc() {
                @Override
                public int proc(Char attacker, Char defender, int damage) {
                    return damage/2;
                }
            });
        }

        //격발 이펙트
        CellEmitter.heroCenter(hero.pos).burst(BulletParticle.factory(DungeonTilemap.tileCenterToWorld(cell)), 6);
        CellEmitter.heroCenter(hero.pos).burst(BulletParticle.factory(DungeonTilemap.tileCenterToWorld(oppositeCell)), 6);
        //격발 사운드
        gun1.knockBullet().throwSound();
        gun2.knockBullet().throwSound();
        //탄환 소모
        gun1.useRound();
        gun2.useRound();

        hero.next();

    }
}
