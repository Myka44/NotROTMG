package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.enemy.RegularEnemy;

import java.util.Objects;

/** Adds a behavior or stat enhancement to a regular enemy. */
public abstract class RegularEnemyDecorator {
    private final RegularEnemy wrapped;

    protected RegularEnemyDecorator(RegularEnemy wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    public final RegularEnemy decorate() {
        return decorate(wrapped);
    }

    protected abstract RegularEnemy decorate(RegularEnemy enemy);
}