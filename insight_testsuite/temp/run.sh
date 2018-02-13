#!/bin/bash

javac ./src/com/insight/Solution.java -d ./src/
java -cp ./src/ com.insight.Solution ./input/itcont.txt ./input/percentile.txt ./output/repeat_donors.txt