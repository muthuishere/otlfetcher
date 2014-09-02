package com.otl.reports.test

class SimpleTests {


	def printme(){
		
		ClassLoader loader = this.getClass().getClassLoader();
		File indexLoc = new File(loader.getResource(".."+File.separator).getFile());
		String htmlLoc = indexLoc.getParentFile().getAbsolutePath();
		
		println(htmlLoc)
	}
	static main(args) {
	
		
		SimpleTests st= new SimpleTests()
		st.printme()
		
		
	}

}
