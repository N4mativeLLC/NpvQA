
/*var fileInput = document.getElementById("purlFileName"),
	
readFile = function () {
    var reader = new FileReader();
    //reader.readAsBinaryString(fileInput.files[0]);
    reader.onload = function (e) {
    	//processData(reader.result);
    	var data = e.target.result;
    	console.log(data);
    	processData(data);
        
    };
    // start reading the file. When it is done, calls the onload event defined above.
    reader.readAsBinaryString(fileInput.files[0]);
    //processData(data);
};
	
fileInput.addEventListener('change', readFile);

function getCust_info() {
	var filePath = $('input[type=file]').val().replace(/C:\\fakepath\\/i, '')
	//filePath= $("#purlFileName").val();
	//alert(filePath);
    $.ajax({
        type: "GET",
        crossDomain: true,
        url: filePath,
        //url: "testData20Recs_Limited.csv",
        dataType: "text",
        success: function(data) {processData(data);}
     });
}

function processData(allText) {
    var allTextLines = allText.split(/\r\n|\n/);
    var headers = allTextLines[0].split('|');
    var lines = [];

    for (var i=1; i<allTextLines.length; i++) {
        var data = allTextLines[i].split('|');
        if (data.length == headers.length) {

            var tarr = [];
            for (var j=0; j<headers.length; j++) {
                //tarr.push(headers[j]+":"+data[j]);
            	tarr.push(data[j]);
            }
            lines.push(tarr);
        }
    }
    //alert(lines);
    fillTable(lines);
}*/

var customer_id="";
var customer_name="";
var client_id="";
var project_id="";
var generate_id="";

$("#populateTable").click(function() {
	if ($("#FilterChar").val() != "" && $("#auditUser").val()!=""){
		$('#QAtable').empty();
		getTableData();
		
	}else{
		alert("Please select Filter & User");
	}
});

var vid = document.getElementById("myVideo"); 

function playVid(videoFile) { 
	//window.open(videoFile);
	$("#thumbnail").attr("src",videoFile.replace(".mp4",".jpg"));
	vid.src = videoFile;
	vid.play(); 
	
} 

function pauseVid() { 
    vid.pause(); 
}

function getTableData(){
	
	$('#QAtable').show();
	filterchar= $("#FilterChar").val();
	client_id= $("#clientId").val();
	project_id= $("#projectId").val();
	generate_id= $("#generateId").val();
	
	var table= $('#QAtable').DataTable( {
	    //data: lines,
		destroy: true,
		pageLength: 5,
	    ajax: {
	       url:'/npv/qaData/'+ filterchar+'/'+client_id+'/'+project_id+'/'+ generate_id,
	       dataSrc: 'data'
	    },
	    columnDefs : [
	        { targets : [8],
	          render : function (data, type, row) {
	             return '<a href="#" onclick="playVid(\'' + data + '\')">'+ data +'</a>'
	        	  }
	        },
	    ],
        //String header="clm_lead_id|first_name|account_age|initial|last_name|threeMonthAvg|savings|AccountType|video";

	 	columns: [
	        { title: "Clm_lead_id ",data: 'clm_lead_id'  },
	        { title: "First Name",data: 'first_name'  },
	        { title: "Account Age" ,data:'account_age'},
	        { title: "Initial" ,data:'initial'},
	        { title: "Last Name",data:'last_name' },
	        { title: "Three Month Avg" ,data:'threeMonthAvg'},
	        { title: "Savings",data:'savings' },
	        { title: "Account Type",data:'AccountType' },
	        { title: "Video",data:'video' },
	        
	  ]
   
	} );
	
	$('#QAtable tbody').on( 'click', 'tr', function (){ 
		console.log(table.row(this).data());
		customer_id = table.row(this).data()['clm_lead_id'];
		customer_name = table.row(this).data()['first_name'];
		console.log("cust "+customer_id);
		var abc = isCustQAed(customer_id);
		if (abc !=false){
			if (confirm("This customer's QA is already done, do you want to QA Again?")){
				playVid(table.row(this).data()['video']);
				$("#QAtable tbody tr").removeClass('row_selected');		
				$(this).addClass('row_selected');
			}
			
		}else{
			playVid(table.row(this).data()['video']);
			$("#QAtable tbody tr").removeClass('row_selected');		
			$(this).addClass('row_selected');
			
		}
		
	});
	
}

function isCustQAed(customer_id){
	var resultReturn=''
	$.ajax({
		
		url:'/npv/data/isCustomerQA/'+ customer_id,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
            resultReturn=true;
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		resultReturn = res;
		}
	});
	
	return resultReturn
	
}


$("#submitQaResults").click(function() {
	if ($("#QAResult").val() == false){
		alert("Please fill Failure Reason");
		
	}else if (customer_id != "") {
		saveRow();
		//getTableData();
	}else{
		alert("Please select the row from the table")
	}
});

function saveRow(){
	console.log(customer_id);
	var myObject = {};
	myObject["client_id"]= client_id;
	myObject["project_id"]= project_id;
	myObject["generate_id"]= generate_id;
	myObject["customer_id"]= customer_id;
	myObject["customer_name"]= customer_name;
	myObject["result"]= $("#QAResult").val();
	myObject["failure_reason"]= $("#txtFailReason").val();
	myObject["audit_by"]= $("#auditUser").val();
	//alert(JSON.stringify(myObject));
	pushSaveRow(JSON.stringify(myObject));
}

function pushSaveRow (jsonObj){
	console.log(jsonObj)
	$.ajax({
		url: '/npv/audit/save/'+jsonObj,
     	type: 'PUT',
       	dataType: 'text', //you may use jsonp for cross origin request
       	contentType: "charset=utf-8",
       	error: function(data, res) {
			//alert("not saved");
		},
		crossDomain: true,
       	success: function(data, res) {
			alert("Your Result is logged");
		}
	});
}

function fillProjectId(){
	client_id= document.getElementById('clientId').value;
	select = document.getElementById('projectId');
    removeOptions(select);
    $.ajax({
		url: '/npv/project/' + client_id,
        type: 'GET',
        contentType: "application/json",
        error: function(res) {
			//document.getElementById('updMessageAsset').innerHTML=res.responseText;
      	},
      	dataType: 'json', //you may use jsonp for cross origin request
      	crossDomain: true,
      	success: function(res) {
        	console.log(res);
        	var opt1 = document.createElement('option');
      		opt1.value = "";
      		opt1.innerHTML = "---Select Project---";
      		select.appendChild(opt1);
        	$(res).each(function(i, val) {
           		var opt = document.createElement('option');
           		opt.value = val.project_id;
           		opt.innerHTML = val.project_id + " - " + val.project_details;
           		select.appendChild(opt);
        
         	});
        }
  	});
    
}

function fillGenerateId(){
	client_id= document.getElementById('clientId').value;
	project_id= document.getElementById('projectId').value;
	select = document.getElementById('generateId');
    removeOptions(select);
    $.ajax({
		url: '/npv/generate/' + client_id + '/'+ project_id,
        type: 'GET',
        contentType: "application/json",
        error: function(res) {
			//document.getElementById('updMessageAsset').innerHTML=res.responseText;
      	},
      	dataType: 'json', //you may use jsonp for cross origin request
      	crossDomain: true,
      	success: function(res) {
        	console.log(res);
        	var opt1 = document.createElement('option');
      		opt1.value = "";
      		opt1.innerHTML = "---Select Generate Id---";
      		select.appendChild(opt1);
        	$(res).each(function(i, val) {
           		var opt = document.createElement('option');
           		opt.value = val.generate_id;
           		opt.innerHTML = val.generate_id;
           		select.appendChild(opt);
        
         	});
        }
  	});
    
}

function removeOptions(selectbox){
	   var i;
	   for(i = selectbox.options.length - 1 ; i >= 0 ; i--){
	       selectbox.remove(i);
	   }
	}
