package com.notrotmg.protocol.clienttoserver;

import com.notrotmg.domain.CharacterClass;

import java.util.Objects;

/** Selects the class used when this connection joins the game. */
public record JoinGameCommand(CharacterClass characterClass) implements ClientCommand {
    public JoinGameCommand {
        Objects.requireNonNull(characterClass);
    }
}
