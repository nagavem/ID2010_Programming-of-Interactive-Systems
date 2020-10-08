#!
# 2018-08-22/fki Checked for lab version 7
# 06-feb-2012/FK Update for Ubuntu Linux
# -- ----------------------------------------------------------------
# -- This file is for Unix/Linux systems.
# -- This file starts the http deamon that serves the codebase
# -- ----------------------------------------------------------------

SCRIPT_HOME=$(dirname $0)

LABROOT=${SCRIPT_HOME}/../..

WWWROOT=${LABROOT}/cbs

TOOLJAR=${LABROOT}/lib/tools.jar

PORT=8080

echo "TOOLJAR=$TOOLJAR"
echo "PORT=$PORT"
echo "WWWROOT=$WWWROOT"

cat > httpd.cfg <<EOF
# Autoscript by $0
CODEBASE="http://$(hostname):${PORT}"
EOF

unset CLASSPATH

exec java -jar $TOOLJAR -port $PORT -dir $WWWROOT
