#!/bin/sh

cd $(dirname $0)

cd managed-dependencies/
mvn -Dmaven.repo.local=../local-m2 clean install
cd ..

cd omit-duplicate/
cd level3/
mvn -Dmaven.repo.local=../../local-m2 clean install
cd ..
cd level2/
mvn -Dmaven.repo.local=../../local-m2 clean install
cd ..
cd level1/
mvn -Dmaven.repo.local=../../local-m2 clean install
cd ..
cd base/
mvn -Dmaven.repo.local=../../local-m2 clean install
cd ..
cd ..

cd property-resolution/
mvn -Dmaven.repo.local=../local-m2 clean install
cd ..

cd dependency-exclusion/
mvn -Dmaven.repo.local=../local-m2 clean install
cd ..

