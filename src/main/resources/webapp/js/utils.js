/**
 * 
 */



function showinfo(divid,content){
	
	$( "#"+divid ).html('<div class="notify"><span class="symbol icon-info"></span>' + content +'!</div>')
			

	
}

function showerror(divid,content){
	
	$( "#"+divid ).html('<div class="notify notify-red"><span class="symbol icon-error"></span>' + content +'!</div>')
			
	
}

function showsuccess(divid,content){
	
	
	$( "#"+divid ).html('<div class="notify notify-green"><span class="symbol icon-tick"></span>' + content +'!</div>')
			
	
	
}

function showwarning(divid,content){
	
	$( "#"+divid ).html('<div class="notify notify-yellow"><span class="symbol icon-excl"></span>' + content +'!</div>')
			
	
	
}


function showpage(pageName){
	
	
	/*
	if( pageName !="home"){
		
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
	
