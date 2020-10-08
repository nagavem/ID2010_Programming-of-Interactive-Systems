@echo off
rem -- ----------------------------------------------------------------
rem -- 2018-08-22/fki Lab version 7
rem -- 2014-02-17/FK: Added -Djava.rmi.server.useCodebaseOnly=false
rem -- This file starts the ChatServer on Ms Windows.
rem -- ----------------------------------------------------------------

set CFG=%~dp0.\HTTPD_CFG.BAT

IF NOT EXIST "%CFG%" ECHO File not found %CFG% & Goto :eof
CALL "%CFG%"
SET HTTP=%CODEBASE%

set ROOT=%~dp0..\..

set LIB=%ROOT%/lib

set PCY=%LIB%/policy.all

set JRN=%LIB%/JarRunner.jar

set CBS=%HTTP%/ChatServer.jar

set JAR=%HTTP%/ChatServer.jar

set CLASSPATH=

set CBO=-Djava.rmi.server.useCodebaseOnly=false
set POL=-Djava.security.policy=%PCY%
set CDB=-Djava.rmi.server.codebase=%CBS%

java %CBO% "%POL%" "%CDB%" -jar "%JRN%" "%JAR%" %*
