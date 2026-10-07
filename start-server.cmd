@echo off
setlocal
title NotROTMG Server
cd /d "%~dp0"

echo Starting NotROTMG server...
call mvnw.cmd compile exec:java "-Dexec.mainClass=com.notrotmg.server.ServerLauncher"
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" (
    echo.
    echo Server stopped with exit code %EXIT_CODE%.
    pause
)

exit /b %EXIT_CODE%
