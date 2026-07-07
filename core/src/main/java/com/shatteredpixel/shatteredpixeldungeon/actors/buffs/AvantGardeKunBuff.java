package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EmmisionParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AvantGardeKunBuff extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private int HP = 0;

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

    private static final String ROBOT_HP = "HP";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ROBOT_HP, HP);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        ActionIndicator.setAction(this);
        super.restoreFromBundle(bundle);
        HP = bundle.getInt(ROBOT_HP);
    }

    @Override
    public Visual secondaryVisual() {
        if (this.HP <= 0) return null;
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text( Integer.toString(HP) );
        txt.hardlight(CharSprite.BLUE);
        txt.measure();
        return txt;
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.AVANT_GARDE_KUN_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0xE2A865;
    }

    @Override
    public void doAction() {
        if (!(target instanceof Hero)) {
            detach();
            return;
        }
        Hero hero = (Hero)target;
        if (hero.buff(RobotCooldown.class) != null) {
            return;
        }

        if (hero.buff(OnBoard.class) != null) {
            GameScene.selectCell(selector);
        } else {
            if (this.HP != 0) {
                this.HP = Buff.affect(hero, OnBoard.class).onBoard(hero.lvl, this.HP);
            } else {
                this.HP = Buff.affect(hero, OnBoard.class).newRobot(hero.lvl);
            }
        }
    }

    private CellSelector.Listener selector = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            Hero hero = Dungeon.hero;
            OnBoard buff = hero.buff(OnBoard.class);
            if (target == null) return;
            if (buff == null) return;

            if (target == hero.pos) {
                AvantGardeKunBuff.this.HP = buff.offBoard();
            } else if (Dungeon.level.adjacent(hero.pos, target)) {
                //근접 공격
                meleeAttack(hero, target);
            } else {
                //원거리 공격
                shootGun(hero, target);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(AvantGardeKunBuff.class, "prompt");
        }
    };

    public void shootGun(Hero hero, int cell) {
        ArrayList<Gun> availableGuns = new ArrayList<>();
        for (Gun gun : hero.belongings.getAllItems(Gun.class)) {
            if (gun.round() > 0) {
                availableGuns.add(gun);
            }
        }
        if (availableGuns.isEmpty()) {
            hero.yellW("no_available_guns");
            return;
        }

        hero.busy();

        int shot = 0;
        final int MAX_SHOT = 3;
        while (shot < Math.min(MAX_SHOT, availableGuns.size())) {
            shot++;
            Gun gun = Random.element(availableGuns);
            int finalShot = shot;
            Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, 0.1f * finalShot) {
                @Override
                protected void updateValues(float progress) {}

                @Override
                protected void onComplete() {
                    if (finalShot == MAX_SHOT) {
                        hero.spendAndNext(Actor.TICK);
                    }
                    Gun.Bullet bullet = gun.knockBullet();
                    bullet.setSpecialShot(true);
                    bullet.cast(hero, cell);
                    super.onComplete();
                }
            });
        }
    }

    public void meleeAttack(Hero hero, int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy == null) return;

        Callback callback = new Callback() {
            @Override
            public void call() {
                Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.75f);
                CellEmitter.center(hero.pos).start(EmmisionParticle.FACTORY, 0.05f, 20);
                hero.sprite.idle();
            }
        };
        hero.sprite.attack(cell, callback);
        enemy.sprite.flash();
        Sample.INSTANCE.play(Assets.Sounds.HIT, 0.75f);
        Elastic.pushEnemy(hero, enemy, hero.belongings.weapon(), 3);

        for (Gun gun : hero.belongings.getAllItems(Gun.class)) {
            gun.quickReload();
        }
        hero.spendAndNext(Actor.TICK);
    }

    public void updateRobot(int level) {
        if (this.HP != 0) {
            this.HP = Math.min(this.HP+OnBoard.HP_PER_LVL, OnBoard.BASE_HT+level*OnBoard.HP_PER_LVL);
        }
    }

    public static void onLevelUp(Hero hero) {
        if (hero.buff(AvantGardeKunBuff.class) != null) {
            hero.buff(AvantGardeKunBuff.class).updateRobot(hero.lvl);
        }
        if (hero.buff(OnBoard.class) != null) {
            hero.buff(OnBoard.class).updateRobot(hero.lvl);
        }
    }

    public static class OnBoard extends Buff {
        {
            actPriority = VFX_PRIO;
        }

        public static final int BASE_HT = 20;
        public static final int HP_PER_LVL = 3;
        int HT = BASE_HT;
        int HP = HT;
        int lvl = 0;

        @Override
        public void fx(boolean on) {
            if (target instanceof Hero && target.sprite instanceof HeroSprite){
                if (on) ((HeroSprite)target.sprite).replaceAvantGardeSprite();
                else    ((HeroSprite)target.sprite).updateSprite();
                GameScene.updateAvatar();
            }
        }

        @Override
        public boolean attachTo(Char target) {
            if (!(target instanceof Hero)) return false;
            return super.attachTo(target);
        }

        @Override
        public int icon() {
            return BuffIndicator.AVANT_GARDE_KUN;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (HT - HP) / HT);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(HP);
        }

        public int newRobot(int level) {
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP = this.HT;
            this.lvl = level;
            return 0;
        }

        public void updateRobot(int level) {
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP += HP_PER_LVL;
            this.lvl = level;
        }

        public int onBoard(int level, int HP) {
            this.HT = BASE_HT+level*HP_PER_LVL;
            this.HP = HP;
            this.lvl = level;
            return 0;
        }

        public int offBoard() {
            detach();
            return this.HP;
        }

        public int hit(int damage) {
            HP -= damage - Random.NormalIntRange(0, (int)Math.ceil(lvl/3f));
            if (HP <= 0) {
                detach();
                Buff.affect(target, RobotCooldown.class, RobotCooldown.DURATION);
                return -HP; //로봇이 파괴된 경우 남은 피해를 반환
            }
            return 0; //로봇이 피해를 전부 흡수했을 경우 0을 반환
        }

        private static final String ROBOT_HP = "HP";
        private static final String ROBOT_HT = "HT";
        private static final String ROBOT_LVL = "lvl";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ROBOT_HP, HP);
            bundle.put(ROBOT_HT, HT);
            bundle.put(ROBOT_LVL, lvl);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            HP = bundle.getInt(ROBOT_HP);
            HT = bundle.getInt(ROBOT_HT);
            lvl = bundle.getInt(ROBOT_LVL);
        }
    }

    public static class RobotCooldown extends FlavourBuff {
        public static final float DURATION = 200f;

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }

    public static int processDamage(Char target, int damage, Object src) {
        //hunger damage is not affected by shielding
        if (src instanceof Hunger){
            return damage;
        }

        if (target.buff(AvantGardeKunBuff.OnBoard.class) != null) {
            damage = target.buff(AvantGardeKunBuff.OnBoard.class).hit(damage);
        }
        return damage;
    }
}
