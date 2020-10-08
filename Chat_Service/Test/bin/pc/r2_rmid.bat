@Echo Off
REM -- ----------------------------------------------------------------
REM -- 2018-08-22/fki Lab 1 version 7
REM -- 2014-02-17/FK Adding -C-Djava.rmi.server.useCodebaseOnly=false
REM --               http://docs.oracle.com/javase/7/docs/technotes/
REM --                    guides/rmi/enhancements-7.html
REM -- This file is for starting RMID on a Windows computer.
REM -- ----------------------------------------------------------------

REM -- This file launches the Java RMI daemon which is needed
REM -- to register reggie as an activatable service.
REM -- This file uses Windows NT command extensions.

REM -- INSTRUCTIONS BEFORE USE
REM -- This file belongs in the  bin  directory of the lab file-tree.

Set LABROOT=%~dp0..\..

REM -- The location of the directory where rmid should write its log.

Set LOG=%TEMP%\rmid_log

REM -- The policy file rmid should apply.

Set PCY=%LABROOT%\lib\policy.all

Echo ********************************
Echo Starting the Java RMI daemon
Echo You can shut it down by giving the command    rmid -stop
Echo in a command window. That will also stop activated services.
Echo LOG = %LOG%
Echo PCY = %PCY%

REM ********************************
REM Remove old instances of the log directory. This is not the
REM intended way to start rmid, it is supposed to pick up from the
REM logfiles. But since we are developing here...
REM R[emove]D[irectory] /S[earch the tree] /Q[uietly]
REM ********************************

If Not Exist "%LOG%" Goto LogIsGone
Echo Removing %LOG%
RD /S /Q "%LOG%"
:LogIsGone

Echo ********************************

REM ********************************
REM Command line arguments for rmid
REM 
REM The flag -C-Djava.rmi.server.useCodebaseOnly=false tells rmid to send
REM -Djava.rmi.server.useCodebaseOnly=false to each JVM that it starts as
REM a result of activation. In our case, reggie, the Jini lookup service.
REM ********************************

set CLASSPATH=
set CBO=-C-Djava.rmi.server.useCodebaseOnly=false
set POL=-J-Djava.security.policy=%PCY%
set LGC=-J-Djava.rmi.server.logCalls=true

REM -- The rmi daemon should be run in its own window so that its output
REM -- can be monitored if needed. It prints some lines when it starts,
REM -- and then it prints a lot more when a service registers with it.

Start /Min rmid -log "%LOG%" %CBO% "%POL%" %LGC%
