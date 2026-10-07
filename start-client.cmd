@echo off
setlocal
title NotROTMG Client
cd /d "%~dp0"

echo Starting NotROTMG client...
call mvnw.cmd compile javafx:run
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" (
    echo.
    echo Client stopped with exit code %EXIT_CODE%.
    pause
)

exit /b %EXIT_CODE%
