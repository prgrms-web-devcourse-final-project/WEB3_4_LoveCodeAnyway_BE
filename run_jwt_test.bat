@echo off
echo Running JwtTokenProviderTest...
call gradlew test --tests "com.ddobang.backend.global.security.jwt.JwtTokenProviderTest"
echo Test completed. Check the results.
pause
