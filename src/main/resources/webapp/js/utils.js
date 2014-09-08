/**
 * 
 */



function preparecsv(tblid){
	
	$('#'+ tblid).after('<input id="csv_'+tblid +'" type="hidden"/>');
	
	var container = $('<div>').html($('#'+tblid).html());

	// then use .replaceWith(function()) to modify the HTML structure
	container.find('img').replaceWith(function() { return this.alt; })
	var strAlt = container.html();
	
	
	$rows = $(container).find('tr');
	//$rows = $('#'+ tblid).find('tr');

var csvData = "";

for(var i=0;i<$rows.length;i++){
                var $cells = $($rows[i]).children('th,td'); //header or content cells

                for(var y=0;y<$cells.length;y++){
                    if(y>0){
                      csvData += ",";
                    }
                    var txt = ($($cells[y]).text()).toString().trim();
					if(txt =="")
						console.log($($cells[y]).html())
                   // if(txt.indexOf(',')>=0 || txt.indexOf('\"')>=0 || txt.indexOf('\n')>=0){
                        txt = "\"" + txt.replace(/\"/g, "\"\"") + "\"";
                   // }
                    csvData += txt;
                }
                csvData += '\r\n';
 }
 
 
	
	$('#csv_'+tblid).val(csvData)
}
function downloadcsv(tblid){
	
		$('#repname').val(tblid.replace("tbl",""))
	$('#csv').val($('#csv_'+tblid).val())
	
	
	 $( "#frmcsv" ).submit();
	
}
function showinfo(divid,content){
	
	$( "#"+divid ).html('<div class="alert-box notice"><span>notice: </span>' + content +'</div>')
			


	
}

function showerror(divid,content){
	
	$( "#"+divid ).html('<div class="alert-box error"><span>error: </span>' + content +'</div>')
			
	
}

function showsuccess(divid,content){
	
	
	$( "#"+divid ).html('<div class="alert-box success"><span>success: </span>' + content +'</div>')
			
	
	
}

function showwarning(divid,content){
	
	$( "#"+divid ).html('<div class="alert-box warning"><span>warning: </span>' + content +'</div>')
			
	
	
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
	$( "#site_content" ).load( pageName+".html?session_id="+Math.random() );
	
	$( "div.menu ul li" ).removeClass("selected")
	
	$( "div.menu ul li#" +pageName  ).addClass("selected")
	
	
	
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
	
