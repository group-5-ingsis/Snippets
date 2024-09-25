@echo off

call ./gradlew ktlintFormat

git add .

echo Code formatting complete and changes added.
