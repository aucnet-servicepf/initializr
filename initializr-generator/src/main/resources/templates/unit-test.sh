#!/bin/bash

set -e -u -x

ROOT=${'$'}PWD

cd aucnet-commons
mvn install -DskipTests

cd ../source

if [ "${'$'}{DRY_RUN}" = "true" ]; then
  echo "dry run skip test"
else
  mvn test
fi
