#!
# 2018-08-22/fki Checked for lab version 7
# 16-feb-2013/FK Adding -Djava.rmi.server.useCodebaseOnly=false
# 06-feb-2012/FK Update for Ubuntu Linux
# 07-feb-2008/FK Added USER env variable to HTTP value.
# -- ----------------------------------------------------------------
# -- This file is for Unix/Linux systems.
# -- This file starts the ChatServer from its remote installation point.
# -- ----------------------------------------------------------------

SCRIPT_HOME=$(dirname $0)

ROOT=${SCRIPT_HOME}/../..

LIB=${ROOT}/lib

PCY=${LIB}/policy.all

JRN=${LIB}/JarRunner.jar

CFG=${SCRIPT_HOME}/httpd.cfg

if [ -a $CFG ]; then
    . $CFG
    HTTP=$CODEBASE
fi

CBS=${HTTP}/ChatServer.jar

JAR=${HTTP}/ChatServer.jar

unset CLASSPATH

CBO=-Djava.rmi.server.useCodebaseOnly=false
POL=-Djava.security.policy=$PCY
CDB=-Djava.rmi.server.codebase=$CBS

java $CBO $POL $CDB -jar ${JRN} ${JAR} $*
