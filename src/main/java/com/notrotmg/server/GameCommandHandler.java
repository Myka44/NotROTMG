package com.notrotmg.server;

import com.notrotmg.protocol.clienttoserver.ClientCommand;
import com.notrotmg.protocol.servertoclient.ServerMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/** Routes command types to independently registered handling functions. */
public final class GameCommandHandler {
    private final Map<
            Class<? extends ClientCommand>,
            BiFunction<String, ClientCommand, Optional<ServerMessage>>
    > handlers = new HashMap<>();

    public <C extends ClientCommand> GameCommandHandler register(
            Class<C> commandType,
            BiFunction<String, C, Optional<ServerMessage>> handler
    ) {
        Objects.requireNonNull(commandType);
        Objects.requireNonNull(handler);

        handlers.put(
                commandType,
                (playerId, command) ->
                        handler.apply(playerId, commandType.cast(command))
        );

        return this;
    }

    /** Returns the optional private response produced by the registered command handler. */
    public Optional<ServerMessage> handle(String playerId, ClientCommand command) {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(command);

        BiFunction<String, ClientCommand, Optional<ServerMessage>> handler =
                handlers.get(command.getClass());

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for " + command.getClass().getSimpleName()
            );
        }

        return handler.apply(playerId, command);
    }
}
