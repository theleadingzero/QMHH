#!/bin/sh
cd `dirname $0`
java -Djava.library.path=./opengl/macosx:./opengl/linux64:./opengl/linux32 -jar pond.jar -t 2

