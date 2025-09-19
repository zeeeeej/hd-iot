@echo off
setlocal

set JAVA_HOME=C:\Java\jdk1.8.0_131
set GCC_PATH=C:\msys64\mingw64\bin\gcc.exe
echo Native library built: native-library.dll start ....
%GCC_PATH% -shared -o native-library.dll ^
    -I"%JAVA_HOME%\include" ^
    -I"%JAVA_HOME%\include\win32" ^
    native/src/native-library.c

echo Native library built: native-library.dll