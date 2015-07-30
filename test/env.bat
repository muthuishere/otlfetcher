

echo %CLASSPATH% |findstr /C:"htmlunit-" >nul 2>&1
if not errorlevel 1 (

echo "value"
	
) else (
    FOR /R ./lib %%a in (*.jar) DO CALL :AddToPath %%a
	
	
)




:AddToPath
SET CLASSPATH=%1;%CLASSPATH%
GOTO :EOF


