@echo off

call ./gradlew build

if %ERRORLEVEL% neq 0 (
    echo Build failed, push aborted.
    exit /b 1
)
