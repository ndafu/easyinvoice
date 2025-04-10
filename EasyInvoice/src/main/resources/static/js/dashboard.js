function chartLine(callback) {
	var resp = "";
	$.ajax({
		url: "./getDashboardData",
        method: "POST",
        data: {
		},
        success: function (dat) {
			resp =dat.body;
			console.log(resp);
			callback(resp)
        }					
	});
	return resp;
}

$(function () {
  	'use strict'
	var ticksStyle = {
		fontColor: '#495057',
	    fontStyle: 'bold'
    }
	var originString = location.href;
	if(originString.includes("dashboard","login")){
  		var mode = 'index'
  		var intersect = true
		var $salesChart = $('#sales-chart')
  		console.log('____********* zith callback___ : '+chartLine());
  		var key = ['A','B','C'];
  		var value = '';
	 	$.ajax({
			url: "./getDashboardData",
	        method: "POST",
	        data: {
			},
	        success: function (dat) {
	        	console.log(dat);
				key=dat.body[0];
				value=dat.body[1];
				var salesChart = new Chart($salesChart, {
					type: 'bar',
					data: {
			  			labels: key,
			  			datasets: [
			        		{
			          			backgroundColor: 'rgba(10, 148, 31, 10)',
			          			borderColor: 'rgba(10, 148, 31, 10)',
			          			data: value
			        		}
			  			]
					},
			    	options: {
			      		maintainAspectRatio: false,
			      		tooltips: {
			        		mode: mode,
			        		intersect: intersect
			      		},
			      		hover: {
			        		mode: mode,
			       			intersect: intersect
			      		},
			      		legend: {
			        		display: false
			      		},
			      		scales: {
			        		yAxes: [{
				          		// display: false,
				          		gridLines: {
									display: true,
									lineWidth: '4px',
									color: 'rgba(0, 0, 0, .2)',
									zeroLineColor: 'transparent'
								},
								ticks: $.extend({
									beginAtZero: true,
									// Include a dollar sign in the ticks
									callback: function (value) {
										if (value >= 1000) {
											value /= 1000
											value += 'k'
										}
										return 'BIF' + value
									}
								}, ticksStyle)
							}],
							xAxes: [{
							display: true,
						ticks: ticksStyle
						}]
					}
				}
			})
	        }					
		});
	 	$.ajax({
			url: "./getDashboardExpenseData",
	        method: "POST",
	        data: {
			},
	        success: function (dat) {
				key=dat.body[0];
				value=dat.body[1];
				var $visitorsChart = $('#visitors-chart')
				var visitorsChart = new Chart($visitorsChart, {
					data: {
						labels: key,
						datasets: [{
							type: 'line',
							data: value,
							backgroundColor: 'transparent',
							borderColor: 'rgba(10, 148, 31, 10)',
							pointBorderColor: '#007bff',
							pointBackgroundColor: 'rgba(242, 30, 6, 10)',
							fill: false,
							pointHoverBackgroundColor: '#007bff',
							pointHoverBorderColor    : 'rgba(242, 30, 6, 10)'
		      			}
		//      			,
		//      			{
		//					type: 'line',
		//					data: [60, 80, 70, 67, 80, 77, 100],
		//					backgroundColor: 'tansparent',
		//					borderColor: '#ced4da',
		//					pointBorderColor: '#ced4da',
		//					pointBackgroundColor: '#ced4da',
		//					fill: false
		//					// pointHoverBackgroundColor: '#ced4da',
		//					// pointHoverBorderColor    : '#ced4da'
		//				}
						]
		    		},
					options: {
						maintainAspectRatio: false,
						tooltips: {
							mode: mode,
							intersect: intersect
						},
						hover: {
							mode: mode,
							intersect: intersect
						},
						legend: {
							display: false
						},
						scales: {
							yAxes: [{
								gridLines: {
									display: true,
									lineWidth: '4px',
									color: 'rgba(0, 0, 0, .2)',
									zeroLineColor: 'transparent'
								},
								ticks: $.extend({
									beginAtZero: true,
									suggestedMax: 200
								}, ticksStyle)
							}],
							xAxes: [{
								display: true,
								gridLines: {
									display: false
								},
								ticks: ticksStyle
							}]
						}
					}
				})
	        }					
		});
	}
})