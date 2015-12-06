#!/bin/bash
lein cljsbuild once 
git add resources/public/cljsp/main.js
git commit -m 'Updated production cljs file'
git push
