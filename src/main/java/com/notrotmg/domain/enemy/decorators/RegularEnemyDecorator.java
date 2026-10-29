package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.enemy.Enemy;
import com.notrotmg.domain.enemy.EnemyType;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.Projectile;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.WorldBounds;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/** Base wrapper for composable regular-enemy enhancements. */
public abstract class RegularEnemyDecorator extends Enemy {
    protected final Enemy wrapped;

    protected RegularEnemyDecorator(Enemy wrapped) {
        super(
                requireRegularEnemy(wrapped).id(),
                wrapped.name(),
                wrapped.type(),
                wrapped.position(),
                wrapped.size(),
                wrapped.stats(),
                wrapped.projectileSpec(),
                wrapped.attackStrategy(),
                wrapped.projectileBuilderFactory(),
                wrapped.stopDistance(),
                wrapped.attackRange(),
                wrapped.attackCooldownSeconds()
        );
        this.wrapped = wrapped;
    }

    private static Enemy requireRegularEnemy(Enemy enemy) {
        Objects.requireNonNull(enemy);
        if (enemy.type() != EnemyType.REGULAR) {
            throw new IllegalArgumentException("Regular enemy decorators require a regular enemy");
        }
        return enemy;
    }

    @Override public String id() { return wrapped.id(); }
    @Override public void setId(String id) { wrapped.setId(id); }
    @Override public Position position() { return wrapped.position(); }
    @Override protected void position(Position position) { wrapped.setPosition(position); }
    @Override public void setPosition(Position position) { wrapped.setPosition(position); }
    @Override public double size() { return wrapped.size(); }
    @Override public Position center() { return wrapped.center(); }
    @Override public EntityStats stats() { return wrapped.stats(); }
    @Override public int currentHealth() { return wrapped.currentHealth(); }
    @Override public boolean isAlive() { return wrapped.isAlive(); }
    @Override public void takeDamage(double damage) { wrapped.takeDamage(damage); }
    @Override public String name() { return wrapped.name(); }
    @Override public EnemyType type() { return wrapped.type(); }
    @Override public ProjectileSpec projectileSpec() { return wrapped.projectileSpec(); }
    @Override public AttackStrategy attackStrategy() { return wrapped.attackStrategy(); }
    @Override public Supplier<ProjectileBuilder> projectileBuilderFactory() {
        return wrapped.projectileBuilderFactory();
    }
    @Override public double stopDistance() { return wrapped.stopDistance(); }
    @Override public double attackRange() { return wrapped.attackRange(); }
    @Override public double attackCooldownSeconds() { return wrapped.attackCooldownSeconds(); }
    @Override public List<RegularEnemyAccessory> accessories() { return wrapped.accessories(); }

    @Override
    public List<Projectile> attack(Vector2 aimDirection) {
        return super.attack(aimDirection);
    }

    @Override
    public List<Projectile> update(Position targetCenter, double deltaSeconds, WorldBounds bounds) {
        return super.update(targetCenter, deltaSeconds, bounds);
    }
}