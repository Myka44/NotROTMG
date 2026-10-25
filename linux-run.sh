#!/bin/bash
./mvnw compile

./mvnw exec:java "-Dexec.mainClass=com.notrotmg.server.ServerLauncher" &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

./mvnw exec:java "-Dexec.mainClass=com.notrotmg.client.ClientLauncher" &
CLIENT1_PID=$!
echo "Client 1 PID: $CLIENT1_PID"

./mvnw exec:java "-Dexec.mainClass=com.notrotmg.client.ClientLauncher" 
CLIENT2_PID=$!
echo "Client 2 PID: $CLIENT2_PID"

kill "$CLIENT1_PID" "$CLIENT2_PID" "$SERVER_PID"
