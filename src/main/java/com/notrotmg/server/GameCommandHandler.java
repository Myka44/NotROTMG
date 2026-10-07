package com.notrotmg.server;

import com.notrotmg.protocol.clienttoserver.ClientCommand;
import com.notrotmg.protocol.clienttoserver.DropItemCommand;
import com.notrotmg.protocol.clienttoserver.EquipItemCommand;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import com.notrotmg.protocol.clienttoserver.UnequipItemCommand;

import java.util.Objects;

/** Routes authenticated client commands to the appropriate game-world operation. */
public final class GameCommandHandler {
    private final AuthoritativeGameWorld world;

    public GameCommandHandler(AuthoritativeGameWorld world) {
        this.world = Objects.requireNonNull(world);
    }

    /**
     * @return true when the command changed private inventory state that should be sent to its owner
     */
    public boolean handle(String playerId, ClientCommand command) {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(command);

        if (command instanceof PlayerInput input) {
            world.acceptInput(playerId, input);
            return false;
        }
        if (command instanceof EquipItemCommand equipItem) {
            world.equipItem(playerId, equipItem.inventorySlot());
            return true;
        }
        if (command instanceof UnequipItemCommand unequipItem) {
            return world.unequipItem(playerId, unequipItem.equipmentSlot());
        }
        if (command instanceof DropItemCommand dropItem) {
            return world.dropItem(playerId, dropItem.inventorySlot());
        }

        throw new IllegalArgumentException(
                "Unsupported client command: " + command.getClass().getSimpleName()
        );
    }
}
