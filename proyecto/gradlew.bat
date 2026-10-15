@echo off
setlocal

set BASE_DIR=%~dp0
set WRAPPER_JAR=%BASE_DIR%\gradle\wrapper\gradle-wrapper.jar

if not exist "%WRAPPER_JAR%" (
  echo Missing gradle\wrapper\gradle-wrapper.jar
  echo Run "gradle wrapper --gradle-version %1" in this project or restore the wrapper jar.
  exit /b 1
)

set JAVA_EXE=java.exe
if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=gradlew" -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
set "GRADLE_EXIT_CODE=%ERRORLEVEL%"
endlocal & exit /b %GRADLE_EXIT_CODE%
