#!/bin/bash

export PATH="/usr/local/opt/openjdk@8/bin:$PATH"
export CPPFLAGS="-I/usr/local/opt/openjdk@8/include"

npx shadow-cljs release fp

ls -lh ./src/main/resources/public/js/fp.js
