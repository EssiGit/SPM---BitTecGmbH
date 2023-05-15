function createChart(tableName, yName, xValues, yValues, maxYvalue){	
	
	//setze höhe,breite und abstaende 120 normal
	var margin = {top: 30, right: 30, bottom: 120, left: 100},
    width = 1800 - margin.left - margin.right,
    height = 900 - margin.top - margin.bottom;

    //Grafik an chart-container hängen
	var svg = d3.selectAll("#my_dataviz")
		.append("svg")
		.attr("width", width + margin.left + margin.right)
			.attr("height", height + margin.top + margin.bottom)
			.style("background", "#dedede")
		.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	//X-Achse
	var x = d3.scaleBand()
    		.range([ 0, width ])
    		.domain(xValues)
   	 		.padding(0.2);
	
	//Y-Beschriftung	
	svg.append('text')
	.attr('x', -75)
	.attr('y', 0)
	.attr('text-anchor', 'left')
	.text(yName)
		.style("font-size","25px");
	
	//X Achse zum svg hinzufuegen
	svg.append("g")
    	.attr("transform", "translate(0," + height + ")")
    	.call(d3.axisBottom(x))
    	.selectAll("text")
      		.attr("transform", "translate(-10,0)rotate(-45)")
      		.style("text-anchor", "end")
			.style("font-size","25px");
		
	//Y Achse erstellen
	var y = d3.scaleLinear()
    	.domain([0, Math.round(maxYvalue * 1.3)])
    	.range([ height, 20])
    	
	//Y Achse hinzufuegen
  	svg.append("g")
    	.call(d3.axisLeft(y))
		.style("font-size","25px");
		
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
      	.attr("fill", "#69b3a2")

	//Überschrift der Grafik
    svg.append('text')
		.attr('x', width / 2 + margin.left)
		.attr('y', margin.top / 2)
		.attr('text-anchor', 'middle')
		.text(tableName)
 		.style("font-size","25px");
}