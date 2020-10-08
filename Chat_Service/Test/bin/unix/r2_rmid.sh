#!
# 2018-08-22/fki Checked for lab version 7
# 16-feb-2013/FK Adding -C-Djava.rmi.server.useCodebaseOnly=false
#                http://docs.oracle.com/javase/7/docs/technotes/
#                     guides/rmi/enhancements-7.html
# 06-feb-2012/FK Update for Ubuntu Linux
# -- ----------------------------------------------------------------
# -- This file is for Unix/Linux systems.
# -- This file starts the rmi deamon
# -- ----------------------------------------------------------------

SCRIPT_HOME=$(dirname $0)

LABROOT=${SCRIPT_HOME}/../..

LOG=/var/tmp/rmid_log

PCY=${LABROOT}/lib/policy.all

echo "********************************"
echo "Starting the Java RMI daemon"
echo "You can shut it down by giving the command    rmid -stop"
echo "in a command window. That will also stop activated services."
echo "LOG = $LOG"
echo "PCY = $PCY"

if [ -d $LOG ]; then
    echo "Removing $LOG"
    rm -r $LOG
fi

CBO=-C-Djava.rmi.server.useCodebaseOnly=false
POL=-J-Djava.security.policy=$PCY
LGC=-J-Djava.rmi.server.logCalls=true

exec rmid -log $LOG $CBO $POL $LGC

