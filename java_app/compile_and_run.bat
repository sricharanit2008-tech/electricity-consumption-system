@echo off
REM ==============================================================================
REM Scenario 14: Electricity Consumption Management System
REM Windows Compilation and Execution Script
REM ==============================================================================

echo [*] Compiling Java Application...
if not exist "bin" mkdir bin

javac -d bin src\com\electricity\model\*.java src\com\electricity\dao\*.java src\com\electricity\service\*.java src\com\electricity\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [X] Compilation failed. Please ensure JDK 8+ is installed and 'javac' is in your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [✓] Compilation successful!
echo [*] Launching Application...
echo.

java -cp bin com.electricity.Main

pause
