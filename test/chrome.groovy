
	
	
	def process 
	
	def executeOnShell(String command) {
	  return executeOnShell(command, new File(System.properties.'user.dir'))
	}
 
 
 


 boolean isProcessRunning(String serviceName) throws Exception {
 String TASKLIST = "tasklist";
 Process p = Runtime.getRuntime().exec(TASKLIST);
 BufferedReader reader = new BufferedReader(new InputStreamReader(
   p.getInputStream()));
 String line;
 while ((line = reader.readLine()) != null) {

  //System.out.println(line);
  if (line.contains(serviceName)) {
   return true;
  }
 }

 return false;

}


 void forcekillProcess(String serviceName) throws Exception {
 String KILL = "taskkill /F /IM ";
 
  Runtime.getRuntime().exec(KILL + serviceName + " /T");

 }

 void killProcess(String serviceName) throws Exception {
 String KILL = "taskkill /IM ";
 
  Runtime.getRuntime().exec(KILL + serviceName + " /T");

 }
 
			
 def execruntime(command) throws Exception{
 	
	Runtime rt = Runtime.getRuntime();
String[] commands = {command};
Process proc = rt.exec(commands);

BufferedReader stdInput = new BufferedReader(new 
     InputStreamReader(proc.getInputStream()));

BufferedReader stdError = new BufferedReader(new 
     InputStreamReader(proc.getErrorStream()));

// read the output from the command
//System.out.println("Here is the standard output of the command:\n");
String s = null;
StringBuffer sb = new StringBuffer()

while ((s = stdInput.readLine()) != null) {
    sb.append(s);
}

// read any errors from the attempted command
System.out.println("Here is the standard error of the command (if any):\n");
while ((s = stdError.readLine()) != null) {
    
	sb.append("Error" + s);
}

	
			
			
			
 }
 def executeOnShell(String command, File workingDir) {
  println command
  //.directory(workingDir)
  process = new ProcessBuilder(addShellPrefix(command))                                    
                                    .redirectErrorStream(true) 
                                    .start()
//  process.inputStream.eachLine {println it}
//  process.waitFor();
 // return process.exitValue()
}

def startchrome(){

	executeOnShell("chrome")

}
def closechrome(){

 try{

 int i= 0
		
	 while(isProcessRunning("chrome.exe")){
	 
	 println ("Killling again")
	 
	 if( ++i < 5)
			killProcess("chrome.exe")
	else{
	
	 println ("force Killling again")
	 forcekillProcess("chrome.exe")
	}
		
			
						 try {

					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
	 }
 
 
 }catch(Exception e){
			
				e.printStackTrace()
			}
			
	

}

 def addShellPrefix(String command) {
  commandArray = new String[1]
  //commandArray[0] = "sh"
  //commandArray[1] = "-c"
  commandArray[0] = command
  return commandArray
}



 startchrome()
  try {
  println("Attempting to sleep process")
        Thread.sleep(10000);
    } catch (InterruptedException ex) {
    }
	
	println("Attempting to kill process")
	 closechrome()
 
 println("killed process")