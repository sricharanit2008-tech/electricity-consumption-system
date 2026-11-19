#!/bin/bash
# ==============================================================================
# Scenario 14: Electricity Consumption Management System
# Linux / macOS Compilation and Execution Script
# ==============================================================================

echo "[*] Compiling Java Application..."
mkdir -p bin

javac -d bin src/com/electricity/model/*.java src/com/electricity/dao/*.java src/com/electricity/service/*.java src/com/electricity/Main.java

if [ $? -eq 0 ]; then
    echo "[✓] Compilation successful!"
    echo "[*] Launching Application..."
    echo ""
    java -cp bin com.electricity.Main
else
    echo "[X] Compilation failed. Please ensure JDK 8+ is installed."
    exit 1
fi
