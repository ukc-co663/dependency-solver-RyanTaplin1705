#!/bin/bash
CLASSPATH=classes:$(ls lib/* | sed 's/ /:/')
java -cp $CLASSPATH Main $@