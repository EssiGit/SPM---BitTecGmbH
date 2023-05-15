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
	$.post('WekaServlet', { sliderValue: value, clusterInfo: info, ajaxUpdate: 1},
		function(response) {
		   updateChart(response.tableName, response.yName, response.xNames, response.yValues, response.yMax, 450);
			
		}).
		fail(function() {
			console.log('Error:', error);

		});
}

//dropdown2 Funtionalität
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


const loading = document.getElementById('loading');
const loadingimg = document.getElementById('loadingimg');
console.log(loadingimg);

/* beim drücken von "zurueck" im browser wird die Sanduhr versteckt */
window.addEventListener("pageshow", function(event) {
	var historyTraversal = event.persisted ||
		(typeof window.performance != "undefined" &&
			window.performance.navigation.type === 2);
	if (historyTraversal) {
		loading.style.display = "none";
		loadingimg.style.display = "none";
		window.location.reload();
	}
});


/* Lädt die Sanduhr */
const forms = document.querySelectorAll('form');
console.log(forms);
forms.forEach(form => {

	form.addEventListener('submit', function() {

		event.preventDefault();
		loading.style.display = "flex";
		loadingimg.style.display = "flex";
		form.submit();

	});
});


const navbar2Buttons = document.querySelectorAll('.nav2-item');

navbar2Buttons.forEach(button => {
	button.addEventListener('click', () => {

		navbar2Buttons.forEach(btn => {
			btn.classList.remove('active');
		});

		button.classList.add('active');
	});
});
