Steps to follow
---------------

1. Install Git (http://git-scm.com/)

2. Add Git to your path variables
   set PATH=%PATH%;%GIT_HOME%\bin;
   optional: if the git protocol fails during bower install apply this setting:
   git config --global url."https://".insteadOf git://

3. Checkout the administration project from https://webgate.ec.europa.eu/CITnet/svn/UNIONVMS/trunk/USM/web/

4. If your environment is behind a proxy edit .bowerrc fle located in \administration\src\main\webapp folder to set proxy variables.
   If you are not behind a proxy remove these variables.
       {
	
         "directory" : "bower_components",
	
         "proxy":"http://proxy:port",
	
         "https-proxy":"http://proxy:port"
}

5. mvn install

6. Navigate in administration\src\main\webapp
   Type grunt serve
   This will start a web server in http://localhost:9001
   Open a browser and go to http://localhost:9001/app
   This is very convenient for development as the code changes automatically reflected on the browser

7. Deploy the produced war file located in \administration\target in an application server.

   Errors during installation
   --------------------------
   In the first run maven needs to download the grunt and bower dependencies into the project
      Grunt dependencies are listed in package.json and eventually stored in node_modules folder
      Bower dependencies are listed in bower.json and eventually stored in bower_components folder

   1. PROXY error   
      
      Possible output: retry Request to https://bower.herokuapp.com/packages/angular-route failed with ETIMEDOUT, retrying in 1.0s
      
      FIX: Verify that .bowerrc has the correct proxy name and port (step 4). 
           If this is correct you may need to set proxy variables manually.  
           In the command line do it like this...
	   SET HTTP_PROXY=http://proxy:port
           SET HTTPS_PROXY=http://proxy:port
   
   2. Git is not installed or not in the path
      Bower needs Git to download and install its packages.	
      Possible output: bower angular#1.0.6  ENOGIT git is not installed or not in the PATH

      FIX: Verify that Git is installed and added to your path.
      Windows: Control Panel -> System -> System properties You can add %GIT_HOME%\bin into the Environment variables in your system properties.
               If you are using a command line type 
               set PATH=%PATH%;%GIT_HOME%\bin;