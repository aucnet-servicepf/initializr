#!/bin/bash

set -e -u -x

ROOT=${'$'}PWD
BUILD_DIR=${'$'}PWD/package

cd source

mvn package -DskipTests -Dskip.surefire.tests

cp target/${artifactId}-0.0.1-SNAPSHOT ${'$'}BUILD_DIR/springboot-app.jar