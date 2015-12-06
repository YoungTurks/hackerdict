#!/bin/bash
git pull
lein with-profile production cljsbuild once production
supervisorctl restart hackerdict_web
