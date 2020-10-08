@echo off
rem --
rem -- This file will run all the install scripts in one go.
rem --
set BAT=%~dp0.
call "%BAT%\install_jini"
call "%BAT%\install_chatclient"
call "%BAT%\install_chatserver"
