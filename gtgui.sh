#!/bin/bash
JAVA_OPTS="-Xms128m -Xmx512m"
CLASSPATH="jar/gtgui.jar:jar/tablerecognizer.jar:jar/jiu.jar"

java $JAVA_OPTS -cp $CLASSPATH de.dfki.trecs.groundtruth.gui.GTGui 

