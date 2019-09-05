#!/bin/sh
JAVA_OPTS="-Xms128m -Xmx512m"
CLASSPATH="jar/gtgui.jar:jar/tablerecognizer.jar:jar/jiu.jar"
if [ $# != "4" ] ; then
 echo "Usage : ./paint_segments.sh input_image output_image ground_truth_file word_box_file"
else
 java $JAVA_OPTS -cp $CLASSPATH de.dfki.trecs.groundtruth.image.GroundTruthImage $1 $2 $3 $4
fi
