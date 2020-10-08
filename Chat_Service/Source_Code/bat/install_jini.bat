@echo off
set BAT=%~dp0.
call "%BAT%\httpdCopy.bat" lib\jini-core.jar
call "%BAT%\httpdCopy.bat" lib\jini-ext.jar
call "%BAT%\httpdCopy.bat" lib\reggie-dl.jar
