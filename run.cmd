@echo off
setlocal

:: Set Java Home (update this path to match your Java installation)
set "JAVA_HOME=C:\Program Files\Java\java17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Set log directory
set "LOG_DIR=logs"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

:: Set current date in YYYYMMDD format
set "CURRENT_DATE=%date:~-4,4%%date:~-10,2%%date:~-7,2%"

:: Create config directory if it doesn't exist
if not exist "config" mkdir "config"

:: Create output directory if it doesn't exist
if not exist "output" mkdir "output"

:: Run the application with external config
java -Dspring.config.location=file:./config/application.properties ^
     -Dlog4j2.configurationFile=file:./config/log4j2.xml ^
     -Dapp.date=%CURRENT_DATE% ^
     -jar target\dbgrab-0.0.1-SNAPSHOT.jar

:: If the output file exists, rename the input file
if exist "output\result_%CURRENT_DATE%.csv" (
    rename "config\input.csv" "input.csv_%CURRENT_DATE%.done"
)

endlocal