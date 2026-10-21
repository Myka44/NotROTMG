package com.notrotmg.protocol.clienttoserver;

/** Requests that the server undo the most recently executed game command. */
public record UndoCommand() implements ClientCommand {
}
