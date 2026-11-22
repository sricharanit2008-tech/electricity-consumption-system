@echo off
title DBMS Module - Electricity Consumption Query Runner
cls

echo [*] Checking Python Environment for DBMS Runner...

set "PYTHON_CMD="

REM Check LibreOffice Python
if exist "C:\Program Files\LibreOffice\program\python.exe" (
    set "PYTHON_CMD=C:\Program Files\LibreOffice\program\python.exe"
    goto :RUN
)

REM Check PATH python
where python >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "PYTHON_CMD=python"
    goto :RUN
)

REM Check py launcher
where py >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "PYTHON_CMD=py"
    goto :RUN
)

echo [X] Python was not found on your system.
pause
exit /b 1

:RUN
echo [✓] Launching DBMS Terminal Query Runner...
echo.
"%PYTHON_CMD%" "%~dp0run_dbms.py"
pause
