#!/bin/bash
find src -name \*.java >javaFile.txt
javac -encoding GBK -d out -cp . @javaFile.txt