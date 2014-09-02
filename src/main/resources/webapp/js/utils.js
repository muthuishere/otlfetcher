/**
 * 
 */


	  
function showpage(pageName){
	
	
	/*
	if( pageName !="index"){
		
		if($( "#sessioncookie" ).val() ==""){
			
			$( "#dialog-form" ).dialog( "open" );
			return
		}
	}
	*/
	$( "#site_content" ).load( pageName+".html" );
	
	$( "ul#menu li" ).removeClass("current")
	
	$( "ul#menu li#" +pageName  ).addClass("current")
	
	
	
}


function validateadmin() {

 var name = $( "#adminname" )
    
     var password = $( "#adminpassword" )
      var allFields = $( [] ).add( name ).add( password );
	  
    var valid = true;
    allFields.removeClass( "ui-state-error" );

valid=false

if($( "#adminname" ).val() == "admin"  &&  $( "#adminpassword" ).val() == "admin"){
valid=true
}

    if ( valid ) {
	  $( "#sessioncookie" ).val('admin')
      
     // dialog.dialog( "close" );
	  $( "#dialog-form" ).dialog( "close" );
    }else{
	alert("Invalid inputs")
	}
    return valid;
  }
	
