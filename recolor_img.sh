#!/bin/sh
JAVA_OPTS="-Xms128m -Xmx512m"
CLASSPATH="jar/gtgui.jar:jar/tablerecognizer.jar:jar/jiu.jar"
if [ $# != "3" ] ; then 
echo "Usage : ./paint_segments.sh input_image output_image enumerator_type(0=Cell,1=Row,2=Column,3=Table,4=RowSpan,5=ColSpan,6=ROWCOLSPAN"
else
 java $JAVA_OPTS -cp $CLASSPATH de.dfki.trecs.groundtruth.image.RecolorImage $1 $2 $3
display $2
fi
