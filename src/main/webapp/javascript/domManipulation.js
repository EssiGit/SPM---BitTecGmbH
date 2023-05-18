//sende submit event
function SubmitEvent(formId) {
	var oForm = document.getElementById(formId);
	if (oForm) {
		showHourglass();
		oForm.submit();
	}
	else {
		alert("DEBUG - could not find element " + formId);
	}
}

//zeige Sanduhr
function showHourglass() {

	const loading = document.getElementById('loading');
	const loadingimg = document.getElementById('loadingimg');
	loading.style.display = "flex";
	loadingimg.style.display = "flex";

}

//sende submits mit Clusterinfos
function submitForm(action, clusterInfo) {
	var form = document.createElement('form');
	form.setAttribute('method', 'post');
	form.setAttribute('action', action);

	var input = document.createElement('input');
	input.setAttribute('type', 'hidden');
	input.setAttribute('name', 'clusterInfo');
	input.setAttribute('value', clusterInfo);

	form.appendChild(input);
	document.body.appendChild(form);

	showHourglass();

	form.submit();
}

function selectButton(button) {
	var selectedButtonInput = document.getElementById("selectedButton");
	selectedButtonInput.value = button.textContent;

	showHourglass();

	document.getElementById("buttonForm").submit();
}

//Chart Values aktuallisieren je nach Clusteranzahl
function loadData(value, info) {
	$.post('WekaServlet', { sliderValue: value, clusterInfo: info, ajaxUpdate: 1 },
		function(response) {
			updateChart(response.tableName, response.yName, response.xNames, response.yValues, response.yMax, 200);

		}).
		fail(function() {
			console.log('Error:', error);

		});
}

//dropdown2 Funtionalität
if (document.getElementById('kundengruppen-link') != null) {
	var kundengruppenLink = document.getElementById('kundengruppen-link');
	var dropdown = document.getElementById('drop2');

	kundengruppenLink.addEventListener('mouseenter', function() {
		dropdown.style.display = 'block';
	});

	kundengruppenLink.addEventListener('mouseleave', function() {
		dropdown.style.display = 'none';
	});

	dropdown.addEventListener('mouseenter', function() {
		dropdown.style.display = 'block';
	});

	dropdown.addEventListener('mouseleave', function() {
		dropdown.style.display = 'none';
	});
}
/*öffnen der Textbox auf marketing.html*/
function toggleTextBox() {
	var textbox = document.getElementById("textbox");
	if (textbox.style.display == "none") {
		textbox.style.display = "block";
	} else {
		textbox.style.display = "none";
	}
}

//hole Textareavalue und schicke sie per POST
function getText() {

	var textareaValue = document.getElementById("marketingTextbox").value;
	var formData = new FormData();
	formData.append("textareaContent", textareaValue);

	// hidden input field welche den Text enthält
	var hiddenInput = document.createElement("input");
	hiddenInput.type = "hidden";
	hiddenInput.name = "marketingText";
	hiddenInput.value = textareaValue;
	document.getElementById("markForm").appendChild(hiddenInput);

	// Form abschicken
	document.getElementById("markForm").submit();
}