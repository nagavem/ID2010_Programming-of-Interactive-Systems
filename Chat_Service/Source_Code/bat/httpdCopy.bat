@rem ** ************************************************************
@rem **
@rem ** Copies files so that they can be served by the HTTPd server.
@rem **
@rem ** This script is called by the install_*.bat scripts.
@rem **
@rem ** ************************************************************
@echo off

SET TGT=%~dp0..\..\test\cbs

XCOPY /F /Z /Y "%1" "%TGT%\%2"
