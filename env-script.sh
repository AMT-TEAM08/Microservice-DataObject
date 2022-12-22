#!/bin/sh

touch .env
{
  printf "AWS_ACCESS_KEY_ID=%s\n" "$ACCESS_KEY"
  printf "AWS_SECRET_ACCESS_KEY=%s\n" "$SECRET_KEY"
} >> .env
