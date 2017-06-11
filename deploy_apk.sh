#!/bin/bash

curl \
-F "status=2" \
-F "notify=0" \
-F "ipa=@app/build/outputs/apk/app-debug.apk" \
-H "X-HockeyAppToken: caacc29b060840328f49715d5a2bb6b5" \
https://rink.hockeyapp.net/api/2/apps/ab6827b5fe584dd4afa6e0c537aaac27/app_versions/upload
