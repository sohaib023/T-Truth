#!/bin/bash
JAVA_OPTS="-Xms128m -Xmx512m"
CLASSPATH="jar/gtgui.jar:jar/tablerecognizer.jar:jar/jiu.jar"
if [ $# != "1" ]; then
echo "Usage: ./gtgui_preview.sh input_dir(containging images and groundtruth xml file with same names)"
else
java $JAVA_OPTS -cp $CLASSPATH de.dfki.trecs.groundtruth.gui.GTGui preview $1 
fi

