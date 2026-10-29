package com.notrotmg.server;

import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.enemy.BossEnemy;
import com.notrotmg.domain.enemy.RegularEnemy;
import com.notrotmg.protocol.clienttoserver.AttackInput;
import com.notrotmg.protocol.servertoclient.CombatEvent;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatEventsTest {
    @Test
    void emitsOneShotEventForEightBossProjectilesAndDrainsItOnce() {
        AuthoritativeGameWorld world = emptyWorld();
        world.addPlayer("player-1", CharacterClass.WARRIOR);
        world.spawn(new BossEnemy("boss", new Position(220, 64)));

        GameSnapshot snapshot = world.tick();
        List<CombatEvent> events = world.drainCombatEvents();

        assertEquals(8, snapshot.projectiles().size());
        assertEquals(List.of(new CombatEvent(CombatEvent.Kind.ENEMY_SHOT, "guardian-projectile")), events);
        assertTrue(world.drainCombatEvents().isEmpty());
        world.tick();
        assertTrue(world.drainCombatEvents().isEmpty());
    }

    @Test
    void emitsAHitEvenWhenTheKilledEnemyIsAlreadyAbsentFromTheSnapshot() {
        AuthoritativeGameWorld world = emptyWorld();
        world.addPlayer("player-1", CharacterClass.WARRIOR);
        RegularEnemy enemy = new RegularEnemy("target", new Position(110, 64));
        enemy.takeDamage(50);
        world.spawn(enemy);
        world.acceptAttackInput("player-1", new AttackInput(124, 78, true));
        List<CombatEvent> events = new ArrayList<>();

        for (int tick = 0; tick < 30 && enemy.isAlive(); tick++) {
            world.tick();
            events.addAll(world.drainCombatEvents());
        }

        assertTrue(world.snapshot().enemies().isEmpty());
        assertEquals(1, count(events, CombatEvent.Kind.PLAYER_SHOT));
        assertEquals(1, count(events, CombatEvent.Kind.ENEMY_HIT));
    }

    @Test
    void distinguishesPlayerDamageFromEnemyDamage() {
        AuthoritativeGameWorld world = emptyWorld();
        world.addPlayer("player-1", CharacterClass.WARRIOR);
        world.spawn(new RegularEnemy("attacker", new Position(110, 64)));
        List<CombatEvent> events = new ArrayList<>();

        for (int tick = 0; tick < 15; tick++) {
            world.tick();
            events.addAll(world.drainCombatEvents());
        }

        assertEquals(1, count(events, CombatEvent.Kind.PLAYER_HIT));
        assertEquals(0, count(events, CombatEvent.Kind.ENEMY_HIT));
        var player = world.snapshot().players().get(0);
        assertTrue(player.currentHealth() < player.maxHealth());
    }

    @Test
    void spawningAndUndoingAnEnemyDoesNotCountAsDamage() {
        AuthoritativeGameWorld world = emptyWorld();
        world.executeCommand(new SpawnEnemyCommand(
                world,
                new RegularEnemy("temporary", new Position(110, 64))
        ));
        assertTrue(world.undoLastCommand());
        world.tick();

        assertTrue(world.drainCombatEvents().isEmpty());
    }

    private AuthoritativeGameWorld emptyWorld() {
        AuthoritativeGameWorld world = new AuthoritativeGameWorld();
        world.despawn("slime-1");
        world.despawn("boss-1");
        return world;
    }

    private long count(List<CombatEvent> events, CombatEvent.Kind kind) {
        return events.stream().filter(event -> event.kind() == kind).count();
    }
}
