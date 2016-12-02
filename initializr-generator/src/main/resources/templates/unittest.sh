#!/bin/bash

set -e -u -x

ROOT=${'$'}PWD

cd source

mvn test
