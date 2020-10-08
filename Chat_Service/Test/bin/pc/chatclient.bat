@echo off
rem -- ----------------------------------------------------------------
rem -- 2018-08-22/fki Lab version 7
rem -- 2014-02-17/FK: Added -Djava.rmi.server.useCodebaseOnly=false
rem -- This file starts the ChatClient on Windows.
rem -- ----------------------------------------------------------------

set CFG=%~dp0.\HTTPD_CFG.BAT

IF NOT EXIST "%CFG%" ECHO File not found %CFG% & Goto :eof

CALL "%CFG%"
SET HTTP=%CODEBASE%

set ROOT=%~dp0..\..

set LIB=%ROOT%/lib

set PCY=%LIB%/policy.all

set JRN=%LIB%/JarRunner.jar

set CBS=%HTTP%/ChatClient.jar

set JAR=%HTTP%/ChatClient.jar

rem -- The ChatClient itself does not do any explicit logging, but
rem -- the Jini classes it uses may do so.
rem -- If you want logging, uncomment the next two lines.
rem -- To change the amount of logging, edit the properties file.

rem set LOG=chatclient-logging.properties
rem set DLOG=-Djava.util.logging.config.file=%LOG%

set CLASSPATH=

set CBO=-Djava.rmi.server.useCodebaseOnly=false
set POL=-Djava.security.policy=%PCY%
set CDB=-Djava.rmi.server.codebase=%CBS%

IF DEFINED DLOG (
  java "%DLOG%" %CBO% "%POL%" "%CDB%" -jar "%JRN%" "%JAR%" %*
) ELSE (
  java %CBO% "%POL%" "%CDB%" -jar "%JRN%" "%JAR%" %*
)
