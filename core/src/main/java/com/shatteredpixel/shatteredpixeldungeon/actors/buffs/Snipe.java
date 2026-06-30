package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnipeAreaParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnipeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Snipe extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ActionIndicator.setAction(this);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.TELESCOPE_ACTION;
    }

    @Override
    public int indicatorColor() {
        return 0xCC6C93;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(selector);
    }

    private CellSelector.Listener selector = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            Hero hero = Dungeon.hero;
            if (target != null) {
                if (!Dungeon.level.passable[target]) {
                    hero.yellW("cannot_see_wall");
                    return;
                }
                if (hero.buff(ScopedArea.class) != null) {
                    hero.buff(ScopedArea.class).detach();
                }

                ArrayList<Integer> area = new ArrayList<>();

                for (int i : PathFinder.NEIGHBOURS9) {
                    if (!Dungeon.level.passable[target + i]) continue;
                    area.add(target + i);
                    Char ch = Actor.findChar(target + i);
                    if (ch instanceof Mob && ch.alignment == Char.Alignment.ENEMY
                            && hero.hasTalent(Talent.MIYU_EX1_1)
                            && Dungeon.level.distance(hero.pos, ch.pos) >= 12-3*hero.pointsInTalent(Talent.MIYU_EX1_1)) {
                        Buff.prolong(ch, Slow.class, 2f);
                    }
                }
                if (hero.hasTalent(Talent.MIYU_EX1_3) && hero.belongings.weapon() instanceof Gun) {
                    ((Gun) hero.belongings.weapon()).manualReload(hero.pointsInTalent(Talent.MIYU_EX1_3), true);
                }
                Buff.affect(hero, ScopedArea.class).setup(area, 10f, target, Dungeon.depth, Dungeon.branch);
                hero.sprite.operate(target);
                hero.next();
                Dungeon.observe();
                detach();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Snipe.class, "prompt");
        }
    };

    public static class ScopedArea extends Buff {
        public int pos, depth, branch;
        public float left = 0;
        public float max = 0;

        private ArrayList<Integer> areaPositions = new ArrayList<>();
        private ArrayList<Emitter> pathEmitters = new ArrayList<>();

        @Override
        public void detach() {
            GameScene.updateFog(pos, 2);
            super.detach();
        }

        @Override
        public void fx(boolean on) {
            if (on){
                for (int i : areaPositions){
                    Emitter e = CellEmitter.get(i);
                    e.pour(SnipeAreaParticle.FACTORY, 0.5f);
                    pathEmitters.add(e);
                }
            } else {
                for (Emitter e : pathEmitters){
                    e.on = false;
                }
                pathEmitters.clear();
            }
        }

        @Override
        public int icon() {
            return BuffIndicator.SNIPE;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (max - left) / max);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.decimalFormat("#.##", left));
        }

        @Override
        public String iconTextDisplay() {
            return Messages.decimalFormat("#.##", left);
        }

        public void setup(ArrayList<Integer> area, float duration, int pos, int depth, int branch){
            this.areaPositions = area;

            if (target != null) {
                fx(false);
                fx(true);
            }

            this.left = this.max = duration;
            this.pos = pos;
            this.depth = depth;
            this.branch = branch;
        }

        @Override
        public boolean act() {
            if (Dungeon.depth != this.depth){
                detach();

                spend(TICK);
                return true;
            }

            left--;

            BuffIndicator.refreshHero();
            if (left <= 0){
                detach();
            }

            spend(TICK);
            return true;
        }

        private static final String AREA_POSITIONS = "areaPositions";
        private static final String BRANCH = "branch";
        private static final String DEPTH = "depth";
        private static final String POS = "pos";
        private static final String LEFT = "left";
        private static final String MAX = "max";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            int[] values = new int[areaPositions.size()];
            for (int i = 0; i < values.length; i ++)
                values[i] = areaPositions.get(i);
            bundle.put(AREA_POSITIONS, values);

            bundle.put(DEPTH, depth);
            bundle.put(BRANCH, branch);
            bundle.put(POS, pos);
            bundle.put(LEFT, left);
            bundle.put(MAX, max);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            int[] values = bundle.getIntArray( AREA_POSITIONS );
            for (int value : values) {
                areaPositions.add(value);
            }

            depth = bundle.getInt(DEPTH);
            branch = bundle.getInt(BRANCH);
            pos = bundle.getInt(POS);
            left = bundle.getFloat(LEFT);
            max = bundle.getFloat(MAX);
        }

        public boolean posInArea(int pos) {
            for (int i : areaPositions) {
                if (i == pos)
                    return true;
            }
            return false;
        }

        public void snipe(int cell, Char ch, Gun.Bullet bullet) {
            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                Dungeon.hero.busy();
                Dungeon.hero.sprite.zap(cell, new Callback() {
                    @Override
                    public void call() {
                        Callback callback = new Callback() {
                            @Override
                            public void call() {
                                Dungeon.hero.spendAndNext(bullet.castDelay(Dungeon.hero, cell));
                            }
                        };
                        bullet.setSnipeShot(true);
                        CellEmitter.center(cell).burst(SnipeParticle.factory(ch, bullet, callback), 1);
                    }
                });
            }
        }

    }

}
