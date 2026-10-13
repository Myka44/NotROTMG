package com.notrotmg.protocol.clienttoserver;

/** Current mouse target and firing state for one player. */
public record AttackInput(
        double targetX,
        double targetY,
        boolean firing
) implements ClientCommand {
    public AttackInput {
        if (!Double.isFinite(targetX) || !Double.isFinite(targetY)) {
            throw new IllegalArgumentException("Attack target coordinates must be finite");
        }
    }
}
