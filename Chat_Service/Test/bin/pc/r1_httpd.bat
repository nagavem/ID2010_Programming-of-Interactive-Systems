@Echo Off
REM -- ----------------------------------------------------------------
REM -- 2018-08-22/fki Lab version 7
REM --
REM -- This file is for Windows
REM -- Use this script to start the codebase server on your computer.
REM -- ----------------------------------------------------------------

REM -- This command file launches the HTTP server.
REM -- It is used for two things:
REM --   1) Serving up Java jar-files for the JarRunner launcher program.
REM --   2) Acting as a codebase server for Java RMI applications.
REM --
REM -- The codebase property of a JVM is a URL attached to objects it
REM -- sends off to remote JVMs. The remote JVM can then consult the
REM -- codebase to load the classes it needs in order to resolve and
REM -- create the recieved object.
REM --
REM -- Fredrik Kilander, KTH

REM -- Set LABROOT to be the test directory (from here, up, up)
Set LABROOT=%~dp0..\..

REM -- Set WWWROOT to the codebase directory

Set WWWROOT=%LABROOT%\cbs

REM -- Set TOOLJAR to the location of the tools.jar file. This file
REM -- contains the HTTP server that came with Jini 1.1.

Set TOOLJAR=%LABROOT%\lib\tools.jar

REM -- Set PORT to the portnumber on which the HTTP server should
REM -- listen for requests. If your system is already running a HTTP
REM -- server you probably want a high-numbered port, like 8000, 8080
REM -- or 8800 if they are available.

Set PORT=8090

Echo TOOLJAR = %TOOLJAR%
Echo PORT    = %PORT%
Echo WWWROOT = %WWWROOT%

Set OUT=HTTPD_CFG.BAT
echo @ECHO OFF                                 > %OUT%
echo REM AUTO-SCRIPT BY %~dpnx0               >> %OUT%
echo SET CODEBASE=http://%COMPUTERNAME%:%PORT%>> %OUT%

REM -- No classpath by variable.

Set CLASSPATH=

REM -- The small HHTP server does not print anything, but it is nice to
REM -- have a window that can be closed to shut it down. This command
REM -- line starts a new, minimized window for the server.
start "httpd" /min java -jar "%TOOLJAR%" -port %PORT% -dir "%WWWROOT%"

REM -- If you want to run the HTTP server in a window you create yourself,
REM -- then use this instead:
REM java -jar "%TOOLJAR%" -port %PORT% -dir "%WWWROOT%"

REM -- The following commands starts the HTTP server in the background,
REM -- without a window. To shut it down you need use the Windows Task
REM -- Manager.
REM start /b java -jar "%TOOLJAR%" -port %PORT% -dir "%WWWROOT%"
