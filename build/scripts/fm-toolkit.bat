@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  fm-toolkit startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and FM_TOOLKIT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\fm-toolkit-0.1.jar;%APP_HOME%\lib\joda-time-2.9.3.jar;%APP_HOME%\lib\okhttp-2.7.5.jar;%APP_HOME%\lib\okhttp-urlconnection-2.7.5.jar;%APP_HOME%\lib\slf4j-nop-1.7.21.jar;%APP_HOME%\lib\bcel-5.2.jar;%APP_HOME%\lib\gitlab-api-2.0.4.jar;%APP_HOME%\lib\github-api-1.73.jar;%APP_HOME%\lib\org.eclipse.jgit-4.3.0.201604071810-r.jar;%APP_HOME%\lib\okio-1.6.0.jar;%APP_HOME%\lib\slf4j-api-1.7.21.jar;%APP_HOME%\lib\jakarta-regexp-1.4.jar;%APP_HOME%\lib\jackson-jaxrs-1.9.13.jar;%APP_HOME%\lib\jersey-client-2.14.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\commons-codec-1.7.jar;%APP_HOME%\lib\jackson-databind-2.2.3.jar;%APP_HOME%\lib\commons-io-1.4.jar;%APP_HOME%\lib\bridge-method-annotation-1.14.jar;%APP_HOME%\lib\jsch-0.1.53.jar;%APP_HOME%\lib\JavaEWAH-0.7.9.jar;%APP_HOME%\lib\httpclient-4.3.6.jar;%APP_HOME%\lib\jackson-core-asl-1.9.13.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.13.jar;%APP_HOME%\lib\jersey-common-2.14.jar;%APP_HOME%\lib\javax.ws.rs-api-2.0.1.jar;%APP_HOME%\lib\hk2-api-2.4.0-b06.jar;%APP_HOME%\lib\javax.inject-2.4.0-b06.jar;%APP_HOME%\lib\hk2-locator-2.4.0-b06.jar;%APP_HOME%\lib\jackson-annotations-2.2.3.jar;%APP_HOME%\lib\jackson-core-2.2.3.jar;%APP_HOME%\lib\annotation-indexer-1.4.jar;%APP_HOME%\lib\httpcore-4.3.3.jar;%APP_HOME%\lib\commons-logging-1.1.3.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\jersey-guava-2.14.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\hk2-utils-2.4.0-b06.jar;%APP_HOME%\lib\aopalliance-repackaged-2.4.0-b06.jar;%APP_HOME%\lib\javassist-3.18.1-GA.jar;%APP_HOME%\lib\javax.inject-1.jar

@rem Execute fm-toolkit
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %FM_TOOLKIT_OPTS%  -classpath "%CLASSPATH%" report/ReportWriter %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable FM_TOOLKIT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%FM_TOOLKIT_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
