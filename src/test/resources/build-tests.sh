#!/bin/sh

cd $(dirname $0)

cd managed-dependencies/
mvn -Dmaven.repo.local=../local-m2 clean install || exit 1
cd ..

cd omit-duplicate/
cd level3/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd level2/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd level1/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd base/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd ..

cd property-resolution/
mvn -Dmaven.repo.local=../local-m2 clean install || exit 1
cd ..

cd dependency-exclusion/
mvn -Dmaven.repo.local=../local-m2 clean install || exit 1
cd ..

cd optional-dependencies/
mvn -Dmaven.repo.local=../local-m2 clean install || exit 1
cd ..

cd dependency-exclusion2/
cd lib/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd dep1/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd dep2/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd base/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd ..

cd optional-dependencies2/
cd lib-v1/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd lib-v2/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd dep1/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd dep2/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd base/
mvn -Dmaven.repo.local=../../local-m2 clean install || exit 1
cd ..
cd ..

