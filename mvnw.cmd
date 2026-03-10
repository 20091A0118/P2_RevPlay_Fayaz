@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Begin all REM://
@echo off
@REM Set title of command window
title %0
@REM Enable extensions
@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if NOT "%JAVA_HOME%"=="" goto OkJHome

for %%i in (java.exe) do set "JAVACMD=%%~$PATH:i"
goto checkJCmd

:OkJHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"

:checkJCmd
if exist "%JAVACMD%" goto chkMHome

echo The JAVA_HOME environment variable is not defined correctly, >&2
echo this environment variable is needed to run this program. >&2
goto error

:chkMHome
set "MAVEN_PROJECTBASEDIR=%~dp0"

@REM Find the project basedir, i.e. the directory that contains the folder ".mvn".
:findBaseDir
IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn" goto :findBaseDirEnd
goto :endFindBaseDir

:findBaseDirEnd
set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"
if "%MAVEN_PROJECTBASEDIR%"=="%MAVEN_PROJECTBASEDIR:~0,-1%" goto error
for %%i in ("%MAVEN_PROJECTBASEDIR%") do set "MAVEN_PROJECTBASEDIR=%%~dpi"
goto findBaseDir

:endFindBaseDir

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

IF NOT EXIST %WRAPPER_JAR% (
    powershell -Command "&{"^
        "$webclient = new-object System.Net.WebClient;"^
        "if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
        "    $webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
        "}"^
        "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;"^
        "$webclient.DownloadFile(%WRAPPER_URL%, '%WRAPPER_JAR%')"^
        "}"
    if "%MVNW_VERBOSE%"=="true" (
        echo Finished downloading %WRAPPER_URL%
    )
)

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
set MAVEN_CMD_LINE_ARGS=%*

@REM Extension to allow automatically downloading the maven-hierarchical-wrapper
set MAVEN_BASEDIR=%MAVEN_PROJECTBASEDIR%
FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%WRAPPER_PROPERTIES%") DO (
    IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
    IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@REM If wrapper jar exists, use it. Otherwise, download Maven directly
IF EXIST %WRAPPER_JAR% (
    "%JAVACMD%" ^
        %JVM_CONFIG_MAVEN_PROPS% ^
        %MAVEN_OPTS% ^
        %MAVEN_DEBUG_OPTS% ^
        -classpath %WRAPPER_JAR% ^
        "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
        org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
) ELSE (
    @REM Fallback: download Maven distribution directly
    set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6"
    IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
        echo Maven wrapper jar not found. Downloading Maven 3.9.6 distribution...
        powershell -Command "&{"^
            "$distributionUrl = '%DISTRIBUTION_URL%';"^
            "$zipFile = '%TEMP%\apache-maven-3.9.6-bin.zip';"^
            "$mavenHome = '%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6';"^
            "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;"^
            "$webclient = new-object System.Net.WebClient;"^
            "Write-Host 'Downloading Maven from' $distributionUrl '...';"^
            "$webclient.DownloadFile($distributionUrl, $zipFile);"^
            "Write-Host 'Extracting...';"^
            "New-Item -ItemType Directory -Path $mavenHome -Force | Out-Null;"^
            "Expand-Archive -Path $zipFile -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force;"^
            "Remove-Item $zipFile -Force;"^
            "Write-Host 'Maven installed successfully.'"^
            "}"
    )
    "%MAVEN_HOME%\bin\mvn.cmd" %MAVEN_CMD_LINE_ARGS%
)

if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%
