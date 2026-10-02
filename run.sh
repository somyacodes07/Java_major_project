#!/bin/bash
# Shell script to compile and run Wholesale Order Processing System
echo "=========================================================="
echo " Starting Wholesale Order Processing System (Case Study 119)"
echo "=========================================================="

mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

if [ $? -eq 0 ]; then
    echo "✔ Compilation successful! Launching GUI Application..."
    java -cp bin com.wholesale.Main
else
    echo "✘ Compilation failed! Please check Java version."
fi
