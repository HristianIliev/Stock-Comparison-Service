$(document).ready(function() {
    $('.chartContainer').each(function(i, obj) {
        var chartInformation = $(this).text();

        var firstStockName = chartInformation.split(":")[0];
        var secondStockName = chartInformation.split(":")[1].split(" ")[0];
        var periods = chartInformation.split(" ")[1].split(" ")[0];
        var type = chartInformation.split("periods ")[1];

        var chartContainer = $(this);

        $.ajax({
            url: "/api/diagrams?firstStockName=" +
                firstStockName +
                "&secondStockName=" +
                secondStockName +
                "&periods=" +
                periods,
            method: "GET",
            success: function(result) {
                var chartHolder = $(chartContainer).siblings("div").first();

                $(chartHolder).append($("<div/>").attr("id", "chart_div" + firstStockName + secondStockName));

                moment.locale("bg");

                var diagramTitle = "Diagram for pair of stocks: " + firstStockName + ":" + secondStockName;
                var chartWidth = $(chartHolder).width();
                var chartHeight = $(chartHolder).height();

                $(function() {
                    if (type === "spread") {
                        var finalData = [];
                        for (var i = result.dataPoints.length - 1; i >= 0; i -= 1) {
                            var array = [];
                            array.push(moment(result.dataPoints[i].time + " 3:00").valueOf());
                            array.push(result.dataPoints[i].value);
                            finalData.push(array);
                        }

                        Highcharts.chart("chart_div" + firstStockName + secondStockName, {
                            chart: {
                                width: chartWidth,
                                height: chartHeight
                            },
                            title: {
                                text: diagramTitle
                            },
                            yAxis: {
                                plotLines: [{
                                    color: "red",
                                    value: result.calculations.averageOfDifferences,
                                    width: 3
                                }],
                                title: {
                                    text: "AVG Spread"
                                },
                                labels: {
                                    format: "{value:.2f}"
                                },
                                tickPositioner: function() {
                                    var positions = [
                                        result.calculations.averageOfDifferences - result.calculations.standardDeviationOfDifferences * 3,
                                        result.calculations.averageOfDifferences - result.calculations.standardDeviationOfDifferences * 2,
                                        result.calculations.averageOfDifferences - result.calculations.standardDeviationOfDifferences,
                                        result.calculations.averageOfDifferences,
                                        result.calculations.averageOfDifferences + result.calculations.standardDeviationOfDifferences,
                                        result.calculations.averageOfDifferences + result.calculations.standardDeviationOfDifferences * 2,
                                        result.calculations.averageOfDifferences + result.calculations.standardDeviationOfDifferences * 3
                                    ];
                                    return positions;
                                }
                            },
                            xAxis: {
                                title: {
                                    text: "Time"
                                },
                                type: "datetime",
                                endOnTick: true
                            },
                            legend: {
                                layout: "vertical",
                                align: "right",
                                verticalAlign: "middle"
                            },
                            plotOptions: {
                                series: {
                                    label: {
                                        connectorAllowed: false
                                    },
                                    pointStart: 2010
                                }
                            },
                            series: [{
                                    name: "Spread of difference for the day",
                                    data: finalData
                                },
                                {
                                    name: "Average spread of difference",
                                    color: "red"
                                }
                            ],
                            responsive: {
                                rules: [{
                                    condition: {
                                        maxWidth: 500
                                    },
                                    chartOptions: {
                                        legend: {
                                            layout: "horizontal",
                                            align: "center",
                                            verticalAlign: "bottom"
                                        }
                                    }
                                }]
                            }
                        });
                    } else if (type === "volume") {
                        var finalDataFirst = [];
                        for (var i = result.dataPoints.length - 1; i >= 0; i -= 1) {
                            var array = [];
                            array.push(moment(result.dataPoints[i].time + " 3:00").valueOf());
                            array.push(result.dataPoints[i].firstVolume);
                            finalDataFirst.push(array);
                        }

                        var finalDataSecond = [];
                        for (var i = result.dataPoints.length - 1; i >= 0; i -= 1) {
                            var array = [];
                            array.push(moment(result.dataPoints[i].time + " 3:00").valueOf());
                            array.push(result.dataPoints[i].secondVolume);
                            finalDataSecond.push(array);
                        }

                        Highcharts.chart("chart_div" + firstStockName + secondStockName, {
                            chart: {
                                width: chartWidth,
                                height: chartHeight
                            },
                            title: {
                                text: diagramTitle
                            },
                            yAxis: {
                                plotLines: [{
                                        color: "red",
                                        value: result.calculations.firstAvgVolume,
                                        width: 3
                                    },
                                    {
                                        color: "blue",
                                        value: result.calculations.secondAvgVolume,
                                        width: 3
                                    }
                                ],
                                title: {
                                    text: "Volume in USD"
                                },
                                labels: {
                                    format: "{value:.2f}"
                                }
                            },
                            xAxis: {
                                title: {
                                    text: "Time"
                                },
                                type: "datetime",
                                endOnTick: true
                            },
                            legend: {
                                layout: "vertical",
                                align: "right",
                                verticalAlign: "middle"
                            },
                            plotOptions: {
                                series: {
                                    label: {
                                        connectorAllowed: false
                                    },
                                    pointStart: 2010
                                }
                            },
                            series: [{
                                    name: "Volume of first stock",
                                    data: finalDataFirst
                                },
                                {
                                    name: "Volume of second stock",
                                    data: finalDataSecond
                                },
                                {
                                    name: "Average volume of first stock",
                                    color: "red"
                                },
                                {
                                    name: "Average volume of second stock",
                                    color: "blue"
                                }
                            ],
                            responsive: {
                                rules: [{
                                    condition: {
                                        maxWidth: 500
                                    },
                                    chartOptions: {
                                        legend: {
                                            layout: "horizontal",
                                            align: "center",
                                            verticalAlign: "bottom"
                                        }
                                    }
                                }]
                            }
                        });
                    } else if (type === "price") {
                        var finalDataFirst = [];
                        for (var i = result.dataPoints.length - 1; i >= 0; i -= 1) {
                            var array = [];
                            array.push(moment(result.dataPoints[i].time + " 3:00").valueOf());
                            array.push(result.dataPoints[i].firstPrice);
                            finalDataFirst.push(array);
                        }

                        var finalDataSecond = [];
                        for (var i = result.dataPoints.length - 1; i >= 0; i -= 1) {
                            var array = [];
                            array.push(moment(result.dataPoints[i].time + " 3:00").valueOf());
                            array.push(result.dataPoints[i].secondPrice);
                            finalDataSecond.push(array);
                        }

                        Highcharts.chart("chart_div" + firstStockName + secondStockName, {
                            chart: {
                                width: chartWidth,
                                height: chartHeight
                            },
                            title: {
                                text: diagramTitle
                            },
                            yAxis: {
                                plotLines: [{
                                        color: "red",
                                        value: result.calculations.firstAvgPrice,
                                        width: 3
                                    },
                                    {
                                        color: "blue",
                                        value: result.calculations.secondAvgPrice,
                                        width: 3
                                    }
                                ],
                                title: {
                                    text: "Price in USD"
                                },
                                labels: {
                                    format: "{value:.2f}"
                                }
                            },
                            xAxis: {
                                title: {
                                    text: "Time"
                                },
                                type: "datetime",
                                endOnTick: true
                            },
                            legend: {
                                layout: "vertical",
                                align: "right",
                                verticalAlign: "middle"
                            },
                            plotOptions: {
                                series: {
                                    label: {
                                        connectorAllowed: false
                                    },
                                    pointStart: 2010
                                }
                            },
                            series: [{
                                    name: "Price of first stock",
                                    data: finalDataFirst
                                },
                                {
                                    name: "Price of second stock",
                                    data: finalDataSecond
                                },
                                {
                                    name: "Average price of first stock",
                                    color: "red"
                                },
                                {
                                    name: "Average price of second stock",
                                    color: "blue"
                                }
                            ],
                            responsive: {
                                rules: [{
                                    condition: {
                                        maxWidth: 500
                                    },
                                    chartOptions: {
                                        legend: {
                                            layout: "horizontal",
                                            align: "center",
                                            verticalAlign: "bottom"
                                        }
                                    }
                                }]
                            }
                        });
                    }
                });
            }
        });
    });
});