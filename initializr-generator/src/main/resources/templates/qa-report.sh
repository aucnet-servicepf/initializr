#!/bin/bash

set -e -u -x

ROOT=${'$'}PWD

cd source

mvn site -Dmpir.skip=true


