
# wiremockmemory leak

This relates to the following wiremock python library:
  https://github.com/wiremock/python

### The problem

When running tests such as "test_wiremockmemoryleak.py", we see wiremock is left running after the test completes.  
 
This is observed on a Windows machine with 16 GB of RAM. Our RAM is leaked gradually when rerunning the test.  

### Evidence
1. The wiremock admin URL is still alive after "test_wiremockmemoryleak.py" completes.
2. Using this command:  
    `tasklist | find "java"`  
... you can see that a new 100MB java process is left running each time you run the test 

3. By using the debugging, you can pause the test before it kills wiremock.  
Using tasklist at that point you can see 2 java processes have been started by the test, one around 10 MB and another 100 MB.  
The cleanup of the test removes the 10 MB process but not the 100 MB one.

### Simulate command line run by python aka sample command line:
The wiremock python library runs wiremock in WireMockServer.start  
The following command line is passed to python Popen:

    java -jar C:\python\wiremockmemoryleak\.venv\lib\site-packages\wiremock\server\wiremock-standalone-2.35.1.jar --port 52509 --local-response-templating

Running this on Windows, we can see:
- Run Wiremock is a cmd window using the command above (or similar - ensure the pathnames exist)
- Run `tasklist | find "java"` in another cmd window   
  As Wiremock is running, there are 2 new java processes as described above
- Visit `http://localhost:52509/__admin/` in a web browser. You can see Wiremock is running.
- Use Control+C in the Wiremock window to stop it
- Run `tasklist | find "java"` in another cmd window
  The 2 new java processes are now gone.
- Visit `http://localhost:52509/__admin/` in a web browser. It will timeout, so you can see Wiremock is no longer running.

Therefore, running manually we can see there is no memory leak