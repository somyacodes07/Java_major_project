@echo off
REM Batch script to compile and run Wholesale Order Processing System on Windows
echo ==========================================================
echo  Starting Wholesale Order Processing System (Case Study 119)
echo ==========================================================

if not exist bin mkdir bin
dir /s /B src\*.java > sources.txt
javac -d bin @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo [OK] Compilation successful! Launching GUI Application...
    java -cp bin com.wholesale.Main
) else (
    echo [ERROR] Compilation failed!
)
pause
