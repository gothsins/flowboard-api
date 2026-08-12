#!/bin/sh
# Start script to expose Docker secret as env var JWT_SECRET and then run the app
SECRET_FILE="/run/secrets/jwt_secret"
if [ -f "$SECRET_FILE" ]; then
  export JWT_SECRET=$(cat "$SECRET_FILE")
fi

exec java -jar /app/app.jar
