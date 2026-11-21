@echo off
setlocal enabledelayedexpansion
REM ==============================================================================
REM Scenario 14: Electricity Consumption Management System
REM Windows Compilation and Execution Script
REM ==============================================================================

echo [*] Checking Java Environment...

set "IDE_JAVA=C:\Users\acer\.antigravity-ide\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64\bin\java.exe"

where javac >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "JAVAC_CMD=javac"
    set "JAVA_CMD=java"
) else (
    for /d %%D in ("C:\Program Files\Microsoft\jdk*" "C:\Program Files\Java\jdk*" "C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Amazon Corretto\jdk*" "C:\Program Files\Zulu\zulu*") do (
        if exist "%%D\bin\javac.exe" (
            set "JAVAC_CMD=%%D\bin\javac.exe"
            set "JAVA_CMD=%%D\bin\java.exe"
        )
    )
)

if "%JAVA_CMD%"=="" (
    where java >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        set "JAVA_CMD=java"
    ) else if exist "%IDE_JAVA%" (
        set "JAVA_CMD=%IDE_JAVA%"
        echo [✓] Detected IDE Embedded Java 25 Runtime.
    )
)

if not "%JAVAC_CMD%"=="" (
    echo [*] Compiling Java Application with %JAVAC_CMD%...
    if not exist "bin" mkdir bin
    "%JAVAC_CMD%" -d bin src\com\electricity\Main.java
    if !ERRORLEVEL! EQU 0 (
        echo [✓] Compilation successful!
        echo [*] Launching Application...
        echo.
        "%JAVA_CMD%" -cp bin com.electricity.Main
        goto :END
    )
)

if not "%JAVA_CMD%"=="" (
    echo [*] Launching Application directly via Java source runner...
    echo.
    "%JAVA_CMD%" "%~dp0src\com\electricity\Main.java"
    goto :END
)

echo [X] Java Runtime not found. Please ensure Java is installed.

:END
pause
