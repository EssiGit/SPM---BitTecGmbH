function createChart(tableName, yName, xValues, yValues, maxYvalue, margin, dimension) {

	//setze höhe,breite und abstaende 120 normal
		width = dimension.width - margin.left - margin.right, //1200
		height = dimension.height - margin.top - margin.bottom; //500

	//Grafik an chart-container hängen
	var svg = d3.selectAll("#my_dataviz")
		.append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.style("background", "#f0f8ff")
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	//X-Achse
	var x = d3.scaleBand()
		.range([0, width])
		.domain(xValues)
		.padding(0.2);

	//Y-Beschriftung	
	svg.append('text')
		.attr('x', -75)
		.attr('y', 0)
		.attr('text-anchor', 'left')
		.text(yName)
		.style("font-family", "Arial")
		.style("font-size", "20px");

	//X Achse zum svg hinzufuegen
	svg.append("g")
		.attr("transform", "translate(0," + height + ")")
		.call(d3.axisBottom(x))
		.selectAll("text")
		.attr("transform", "translate(-10,0)rotate(-45)")
		.style("text-anchor", "end")
		.style("font-family", "Arial")
		.style("font-size", "20px");

	//Y Achse erstellen
	var y = d3.scaleLinear()
		.domain([0, Math.round(maxYvalue * 1.3)])
		.range([height, 20])

	//Y Achse hinzufuegen
	svg.append("g")
		.call(d3.axisLeft(y))
		.style("font-family", "Arial")
		.style("font-size", "20px");

	//Balken Hinzufuegen
	svg.selectAll("bar")
		.data(xValues)
		.enter()
		.append("rect")
		.attr("x", function(d) { return x(d) })
		.data(yValues)
		.attr("y", function(d) { return y(d) })
		.attr("width", x.bandwidth())
		.attr("height", function(d) { return height - y(d) })
		.attr("fill", "#98d69c")

	//Überschrift der Grafik
	svg.append('text')
		.attr('x', width / 2 + margin.left)
		.attr('y', margin.top / 2)
		.attr('text-anchor', 'middle')
		.text(tableName)
		.style("font-family", "Arial")
		.style("font-size", "20px");
}

function updateChart(tableName, yName, xValues, yValues, maxYvalue) {
	
	//lösche chart
	d3.select("#my_dataviz svg").remove();
	
	//erstelle neuen chart
	var margin = { top: 30, right: 30, bottom: 400, left: 400 };
	var dimension = { width: 1200, height: 800};
	createChart(tableName, yName, xValues, yValues, maxYvalue, margin, dimension)
}