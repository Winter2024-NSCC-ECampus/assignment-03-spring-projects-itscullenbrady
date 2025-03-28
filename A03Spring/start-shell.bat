@echo off
set USERNAME=admin
set PASSWORD=123
start cmd /k "set USERNAME=%USERNAME% && set PASSWORD=%PASSWORD% && mvn spring-boot:run"