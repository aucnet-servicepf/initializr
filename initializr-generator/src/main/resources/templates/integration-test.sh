#!/bin/bash

set -e -u -x

ROOT=${'$'}PWD

cd source

mvn verify -Dskip.surefire.tests
