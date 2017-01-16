#!/bin/bash

set -e -u -x

ROOT=${'$'}PWD

cd aucnet-commons
mvn install -DskipTests

cd ../source

mvn site -Dmpir.skip=true


