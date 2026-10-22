@echo off
title Premium Calculator
echo ===================================================
echo       Starting Premium Calculator Application...
echo ===================================================

if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-21.0.12.1" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12.1"
    )
)

cd /d "%~dp0"

if exist "target\premium-calculator-1.0.0.jar" (
    echo Launching pre-built JAR...
    echo Web UI will be available at: http://localhost:8080
    start "" "http://localhost:8080"
    java -jar "target\premium-calculator-1.0.0.jar"
) else (
    echo Building and starting with Maven wrapper...
    call mvnw.cmd spring-boot:run
)

pause
