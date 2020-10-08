#!
# 2018-08-22/fki Checked for lab version 7
# 16-feb-2013/FK Adding -Djava.rmi.server.useCodebaseOnly=false
# 06-feb-2012/FK Update for Ubuntu Linux
# -- ----------------------------------------------------------------
# -- This file is for Unix/Linux systems.
# -- This file starts Reggie, the Jini lookup server
# -- ----------------------------------------------------------------

SCRIPT_HOME=$(dirname $0)

LABROOT=${SCRIPT_HOME}/../..

LOG=/var/tmp/reggie_log

PCY=${LABROOT}/lib/policy.all

CFG=${SCRIPT_HOME}/httpd.cfg

if [ -a $CFG ]; then
    . $CFG
    HTTP=$CODEBASE
fi

CBS=${HTTP}/reggie-dl.jar

JAR=${LABROOT}/lib/reggie.jar

echo "******************"
echo "PCY=$PCY"
echo "LOG=$LOG"
echo "CBS=$CBS"
echo "JAR=$JAR"

if [ -d $LOG ]; then
    echo "Removing $LOG"
    rm -r $LOG
fi

unset CLASSPATH

CBO=-Djava.rmi.server.useCodebaseOnly=false

java $CBO -jar $JAR $CBS $PCY $LOG public
