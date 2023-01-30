var intervalResult = null;

$(document).ready(function() {
    var table = $("#table").DataTable({
        paging: false,
        bInfo: false,
        scrollX: true,
        scrollY: "50vh",
        language: {
            search: "Search:",
            sLengthMenu: "Display _MENU_ records"
        },
        colReorder: true
    });

    $("#table_filter")
        .parent()
        .prev()
        .append(
            $("<button/>")
            .attr("type", "button")
            .attr("data-toggle", "modal")
            .attr("data-target", "#addStockModal")
            .addClass("btn")
            .addClass("btn-primary")
            .attr("style", "float: left; margin-top: 10px; margin-left: 10px;")
            .text("Add")
        )
        .append(
            $("<input/>")
            .attr("type", "number")
            .attr("placeholder", "Min")
            .attr("step", ".01")
            .attr(
                "style",
                "display: inline-block; width: 15%; margin-left: 30px; margin-top: 10px;"
            )
            .addClass("form-control")
            .attr("id", "min")
        )
        .append(
            $("<input/>")
            .attr("type", "number")
            .attr("placeholder", "Max")
            .attr("step", ".01")
            .attr(
                "style",
                "display: inline-block; width: 15%; margin-left: 10px; margin-top: 10px;"
            )
            .addClass("form-control")
            .attr("id", "max")
        )
        .append(
            $("<select/>")
            .addClass("custom-select")
            .attr(
                "style",
                "width: 18%;vertical-align: top; margin-left: 10px; margin-top: 10px;"
            )
            .append(
                $("<option/>")
                .attr("value", "Choose parameter")
                .text("Choose parameter")
            )
            .append(
                $("<option/>")
                .attr("value", "1")
                .text("AVG spread.")
            )
            .append(
                $("<option/>")
                .attr("value", "2")
                .text("Mode")
            )
            .append(
                $("<option/>")
                .attr("value", "3")
                .text("Median")
            )
            .append(
                $("<option/>")
                .attr("value", "4")
                .text("Standard deviation")
            )
            .append(
                $("<option/>")
                .attr("value", "5")
                .text("Z-score")
            )
            .append(
                $("<option/>")
                .attr("value", "6")
                .text("Correlation coefficient")
            )
            .append(
                $("<option/>")
                .attr("value", "7")
                .text("Last known price 1")
            )
            .append(
                $("<option/>")
                .attr("value", "8")
                .text("Last known price 2")
            )
            .append(
                $("<option/>")
                .attr("value", "9")
                .text("AVG dividend 1")
            )
            .append(
                $("<option/>")
                .attr("value", "10")
                .text("AVG dividend 2")
            )
            .append(
                $("<option/>")
                .attr("value", "11")
                .text("AVG volume 1")
            )
            .append(
                $("<option/>")
                .attr("value", "12")
                .text("AVG volume 2")
            )
        )
        .append(
            $("<button/>")
            .attr("type", "button")
            .attr(
                "style",
                "vertical-align: top; margin-left: 10px; margin-top: 10px;"
            )
            .addClass("btn")
            .addClass("btn-primary")
            .addClass("filter-button")
            .text("Filter!")
        )
        .append(
            $("<i/>")
            .addClass("fas")
            .addClass("fa-times")
            .addClass("clearFilter")
            .attr("data-toggle", "tooltip")
            .attr("data-placement", "right")
            .attr("title", "Click this icon in order to clear all of the filters")
        );

    table.on("column-reorder", function(e, settings, details) {
        for (var i = 0; i < $(".custom-select option").length; i += 1) {
            var value =
                details.mapping[
                    parseInt($($(".custom-select option")[i]).attr("value"))
                ];

            $($(".custom-select option")[i]).attr("value", value);
        }
    });

    var numberOfTimesWePushed = 0;

    $(".filter-button").click(function() {
        var index = $(".custom-select option:selected").val();
        if (index === "Choose parameter") {
            alert(
                "You must first specify a column to filter in with the dropdown menu"
            );
            return false;
        }

        if (numberOfTimesWePushed > 0) {
            $.fn.dataTable.ext.search.pop();
            numberOfTimesWePushed -= 1;
        }

        $.fn.dataTable.ext.search.push(function(settings, data, dataIndex) {
            var min = parseFloat($("#min").val(), 10);
            var max = parseFloat($("#max").val(), 10);
            var age;
            if (
                data[index] === 0.0 ||
                data[index] === -0.0 ||
                data[index] === "0.0" ||
                data[index] === "-0.0"
            ) {
                age = 0.0;
            } else {
                age = parseFloat(data[index]) || null;
            }

            if (age === null || age === "null") {
                return false;
            }

            if (
                (isNaN(min) && isNaN(max)) ||
                (isNaN(min) && age <= max) ||
                (min <= age && isNaN(max)) ||
                (min <= age && age <= max)
            ) {
                return true;
            }

            return false;
        });

        numberOfTimesWePushed += 1;

        table.draw();

        iziToast.success({
            title: "OK",
            message: "You successfully filtered the table!",
            position: "topRight"
        });
    });

    $(".clearFilter").click(function() {
        while (numberOfTimesWePushed > 0) {
            $.fn.dataTable.ext.search.pop();
            numberOfTimesWePushed -= 1;
        }

        $("#min").val("");
        $("#max").val("");
        $(".custom-select").val("Choose parameter");

        table.draw();
    });

    $(".fa-delete-left").on("click", function(event) {
        var tr = $(this).parent().parent().parent();
        var data = table.row(tr).data();

        var pathEntries = window.location.pathname.split('/');

        var firstStockName = data[0].split(":")[0];
        var secondStockName = data[0].split(":")[1].split(" ")[0]
        $.ajax({
            url: "/api/users/" + pathEntries[2] + "/comparisons/tags?firstStock=" +
                firstStockName +
                "&secondStock=" +
                secondStockName,
            method: "DELETE",
            contentType: "application/json",
            success: function(result) {
                iziToast.success({
                    title: "Success",
                    message: "Tag was deleted for comparison!",
                    position: "topRight"
                });
            }
        });
    });

    $(".dropdownDashboardEntry").on("click", function(event) {
        $('#selectDashboardDropdown').text($(this).val());
        $('#selectDashboardDropdown').val($(this).val());
    });

    $(".dropdownTagEntry").on("click", function(event) {
        $('#existingTagsDropdown').text($(this).val());
        $('#existingTagsDropdown').val($(this).val());

        var classList = document.getElementById($(this).attr('id')).className.split(/\s+/);
        for (var i = 0; i < classList.length; i++) {
            if (classList[i] !== "dropdownTagEntry" && classList[i] !== "dropdown-item") {
                $("#tagColor").val(classList[i]);
            }
        }

        $("#tagColor").prop("disabled", true);
    });

    $(".fa-trash").on("click", function(event) {
        var tr = $(this).parent().parent().parent();
        var data = table.row(tr).data();

        var pathEntries = window.location.pathname.split('/');

        var firstStockName = data[0].split(":")[0];
        var secondStockName = data[0].split(":")[1].split(" ")[0];

        $.ajax({
            url: "/api/users/" + pathEntries[2] + "/comparisons?firstStock=" +
                firstStockName +
                "&secondStock=" +
                secondStockName,
            method: "DELETE",
            success: function(result) {
                iziToast.success({
                    title: "Success",
                    message: "Comparison was successfully deleted!",
                    position: "topRight"
                });
            }
        });
    });

    $(".fa-note-sticky").on("click", function(event) {
        var tr = $(this).parent().parent().parent();
        var data = table.row(tr).data();

        $('#createNewNote').modal('toggle');

        $('#submit-new-note').click(function(event) {
            if (!$('#newNoteTitle').val().length || !$("#newNoteText").val().length) {
                iziToast.error({
                    title: "ERROR",
                    message: "Missing data!",
                    position: "topRight"
                });

                event.preventDefault();

                return;
            }

            $('#createNewNote').modal('toggle');

            var pathEntries = window.location.pathname.split('/');

            var noteTitle = $('#newNoteTitle').val();
            var noteText = $("#newNoteText").val();

            var firstStockName = data[0].split(":")[0];
            var secondStockName = data[0].split(":")[1].split(" ")[0];

            var note = {
                title: noteTitle,
                text: noteText,
                comparison: {
                    firstStockName: firstStockName,
                    secondStockName: secondStockName
                }
            }

            $.ajax({
                url: "/api/users/" + pathEntries[2] + "/notes",
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(note),
                success: function(result) {
                    iziToast.success({
                        title: "Success",
                        message: "Note was saved for comparison!",
                        position: "topRight"
                    });
                }
            });
        });
    });

    $(".fa-tag").on("click", function(event) {
        var tr = $(this).parent().parent().parent();
        var data = table.row(tr).data();

        $('#selectComparisonTag').modal('toggle');

        $('#submit-tag').click(function(event) {
            if (!$('#tagName').val().length && !$("#existingTagsDropdown").val().length) {
                iziToast.error({
                    title: "ERROR",
                    message: "No chart type selected!",
                    position: "topRight"
                });

                event.preventDefault();

                return;
            }

            $('#selectComparisonTag').modal('toggle');

            var pathEntries = window.location.pathname.split('/');

            var tagName = $('#tagName').val();
            if ($("#existingTagsDropdown").val().length) {
                tagName = $("#existingTagsDropdown").val();
            }

            var tag = {
                name: tagName,
                color: $('#tagColor').val()
            }

            var firstStockName = data[0].split(":")[0];
            var secondStockName = data[0].split(":")[1].split(" ")[0];
            $.ajax({
                url: "/api/users/" + pathEntries[2] + "/comparisons/tags?firstStock=" +
                    firstStockName +
                    "&secondStock=" +
                    secondStockName,
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(tag),
                success: function(result) {
                    iziToast.success({
                        title: "Success",
                        message: "Tag was saved for comparison!",
                        position: "topRight"
                    });
                }
            });
        });
    });

    $("#table tbody tr td:not(.tools-cell)").on("click", function() {
        var tr = $(this).parent();
        var data = table.row(tr).data();

        $('#chooseChartTypeModal').modal('toggle')

        $("#submit-chart-type-btn").click(function(event) {
            if (!$('.active-chart-type').length) {
                iziToast.error({
                    title: "ERROR",
                    message: "No chart type selected!",
                    position: "topRight"
                });

                event.preventDefault();

                return;
            }

            $('#chooseChartTypeModal').modal('toggle')

            $(".diagrams-row").html(
                '<div class="loader"></div><span class="loading-label">Generating diagrams for a pair of stocks: ' +
                data[0].split(":")[1].split(" ")[0] +
                "</span>"
            );

            var firstStockName = data[0].split(":")[0];
            var secondStockName = data[0].split(":")[1].split(" ")[0];
            $.ajax({
                url: "/api/diagrams?firstStockName=" +
                    firstStockName +
                    "&secondStockName=" +
                    secondStockName +
                    "&periods=" +
                    $("#periods").val(),
                method: "GET",
                success: function(result) {
                    $(".diagrams-row").html("");

                    $(".diagrams-row").append($("<div/>").attr("id", "chart_div"));

                    moment.locale("bg");

                    var diagramTitle = "Diagram for pair of stocks: " + data[0].split(":")[1].split(" ")[0];
                    var chartWidth = $(".diagrams-row").width();
                    var chartHeight = $(".diagrams-row").height();

                    $(function() {
                        if ($(".active-chart-type").attr('id') === "spreadChartType") {
                            var finalData = [];
                            for (var i = result.dataPoints.length - 1; i >= 0; i -= 1) {
                                var array = [];
                                array.push(moment(result.dataPoints[i].time + " 3:00").valueOf());
                                array.push(result.dataPoints[i].value);
                                finalData.push(array);
                            }

                            Highcharts.chart("chart_div", {
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
                                },
                                exporting: {
                                    buttons: [{
                                        text: 'Save to dashboard',
                                        onclick: function() {
                                            $("#saveToDashboardModal").modal("toggle");

                                            $("#submit-dashboard").on("click", function(event) {
                                                if (!$("#selectDashboardDropdown").val().length) {
                                                    iziToast.error({
                                                        title: "Error",
                                                        message: "Please select a dashboard",
                                                        position: "topRight"
                                                    });

                                                    event.preventDefault();
                                                    $("#saveToDashboardModal").modal("toggle");

                                                    return;
                                                }

                                                var chart = {
                                                    periods: $("#periods").val(),
                                                    firstStockName: firstStockName,
                                                    secondStockName: secondStockName,
                                                    type: "price"
                                                };

                                                var pathEntries = window.location.pathname.split('/');

                                                $.ajax({
                                                    url: "/api/users/" + pathEntries[2] + "/dashboards/" + $("#selectDashboardDropdown").val() + "/charts",
                                                    method: "POST",
                                                    contentType: "application/json",
                                                    data: JSON.stringify(chart),
                                                    success: function(result) {
                                                        $("#saveToDashboardModal").modal("toggle");

                                                        iziToast.success({
                                                            title: "Success",
                                                            message: "Chart was successfully saved",
                                                            position: "topRight"
                                                        });
                                                    },
                                                    error: function(result) {
                                                        $("#saveToDashboardModal").modal("toggle");

                                                        iziToast.error({
                                                            title: "Error",
                                                            message: "Chart already exists",
                                                            position: "topRight"
                                                        });
                                                    }
                                                });
                                            })
                                        },
                                        theme: {
                                            'stroke-width': 1,
                                            stroke: 'silver',
                                            r: 0,
                                            states: {
                                                hover: {
                                                    fill: '#a4edba'
                                                },
                                                select: {
                                                    stroke: '#039',
                                                    fill: '#a4edba'
                                                }
                                            }
                                        }
                                    }]
                                }
                            });
                        } else if ($(".active-chart-type").attr('id') === "volumeChartType") {
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

                            Highcharts.chart("chart_div", {
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
                                },
                                exporting: {
                                    buttons: [{
                                        text: 'Save to dashboard',
                                        onclick: function() {
                                            $("#saveToDashboardModal").modal("toggle");

                                            $("#submit-dashboard").on("click", function(event) {
                                                if (!$("#selectDashboardDropdown").val().length) {
                                                    iziToast.error({
                                                        title: "Error",
                                                        message: "Please select a dashboard",
                                                        position: "topRight"
                                                    });

                                                    event.preventDefault();
                                                    $("#saveToDashboardModal").modal("toggle");

                                                    return;
                                                }

                                                var chart = {
                                                    periods: $("#periods").val(),
                                                    firstStockName: firstStockName,
                                                    secondStockName: secondStockName,
                                                    type: "volume"
                                                };

                                                var pathEntries = window.location.pathname.split('/');

                                                $.ajax({
                                                    url: "/api/users/" + pathEntries[2] + "/dashboards/" + $("#selectDashboardDropdown").val() + "/charts",
                                                    method: "POST",
                                                    contentType: "application/json",
                                                    data: JSON.stringify(chart),
                                                    success: function(result) {
                                                        $("#saveToDashboardModal").modal("toggle");

                                                        iziToast.success({
                                                            title: "Success",
                                                            message: "Chart was successfully saved",
                                                            position: "topRight"
                                                        });
                                                    },
                                                    error: function(result) {
                                                        $("#saveToDashboardModal").modal("toggle");

                                                        iziToast.error({
                                                            title: "Error",
                                                            message: "Chart already exists",
                                                            position: "topRight"
                                                        });
                                                    }
                                                });
                                            })
                                        },
                                        theme: {
                                            'stroke-width': 1,
                                            stroke: 'silver',
                                            r: 0,
                                            states: {
                                                hover: {
                                                    fill: '#a4edba'
                                                },
                                                select: {
                                                    stroke: '#039',
                                                    fill: '#a4edba'
                                                }
                                            }
                                        }
                                    }]
                                }
                            });
                        } else if ($(".active-chart-type").attr('id') === "priceChartType") {
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

                            Highcharts.chart("chart_div", {
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
                                },
                                exporting: {
                                    buttons: [{
                                        text: 'Save to dashboard',
                                        onclick: function() {
                                            $("#saveToDashboardModal").modal("toggle");

                                            $("#submit-dashboard").on("click", function(event) {
                                                if (!$("#selectDashboardDropdown").val().length) {
                                                    iziToast.error({
                                                        title: "Error",
                                                        message: "Please select a dashboard",
                                                        position: "topRight"
                                                    });

                                                    event.preventDefault();
                                                    $("#saveToDashboardModal").modal("toggle");

                                                    return;
                                                }

                                                var chart = {
                                                    periods: $("#periods").val(),
                                                    firstStockName: firstStockName,
                                                    secondStockName: secondStockName,
                                                    type: "price"
                                                };

                                                var pathEntries = window.location.pathname.split('/');

                                                $.ajax({
                                                    url: "/api/users/" + pathEntries[2] + "/dashboards/" + $("#selectDashboardDropdown").val() + "/charts",
                                                    method: "POST",
                                                    contentType: "application/json",
                                                    data: JSON.stringify(chart),
                                                    success: function(result) {
                                                        $("#saveToDashboardModal").modal("toggle");

                                                        iziToast.success({
                                                            title: "Success",
                                                            message: "Chart was successfully saved",
                                                            position: "topRight"
                                                        });
                                                    },
                                                    error: function(result) {
                                                        $("#saveToDashboardModal").modal("toggle");

                                                        iziToast.error({
                                                            title: "Error",
                                                            message: "Chart already exists",
                                                            position: "topRight"
                                                        });
                                                    }
                                                });
                                            })
                                        },
                                        theme: {
                                            'stroke-width': 1,
                                            stroke: 'silver',
                                            r: 0,
                                            states: {
                                                hover: {
                                                    fill: '#a4edba'
                                                },
                                                select: {
                                                    stroke: '#039',
                                                    fill: '#a4edba'
                                                }
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
});

$("#spreadChartType").click(function(event) {
    $("#chartTypeDropdown").text("Spread chart");

    $("#spreadChartType").addClass("active-chart-type");
    $("#volumeChartType").removeClass("active-chart-type");
    $("#priceChartType").removeClass("active-chart-type");
});

$("#volumeChartType").click(function(event) {
    $("#chartTypeDropdown").text("Volume chart");

    $("#spreadChartType").removeClass("active-chart-type");
    $("#volumeChartType").addClass("active-chart-type");
    $("#priceChartType").removeClass("active-chart-type");
});

$("#priceChartType").click(function(event) {
    $("#chartTypeDropdown").text("Price chart");

    $("#spreadChartType").removeClass("active-chart-type");
    $("#volumeChartType").removeClass("active-chart-type");
    $("#priceChartType").addClass("active-chart-type");
});

$("#submit-btn").click(function(event) {
    var firstStock = $("#firstStock").val();
    var secondStock = $("#secondStock").val();

    $(this).html('Add <i class="fas fa-circle-notch fa-spin"></i>');

    if (firstStock.length === 0 || secondStock.length === 0) {
        $(this).html("Add");

        iziToast.error({
            title: "Error",
            message: "There are fields that are empty",
            position: "topRight"
        });
    } else {
        var pathEntries = window.location.pathname.split('/');

        $.ajax({
            url: "/api/users/" + pathEntries[2] + "/comparisons?firstStock=" +
                firstStock +
                "&secondStock=" +
                secondStock,
            method: "POST",
            success: function(result) {
                $("#submit-btn").html("Add");

                $("#addStockModal").modal("hide");
                iziToast.success({
                    title: "OK",
                    message: "Refresh the page in order to see the new pair",
                    position: "topRight"
                });
            },
            error: function(request, status, error) {
                $("#submit-btn").html("Add");

                if (request.status == 409) {
                    iziToast.error({
                        title: "Error",
                        message: "You already have this comparison",
                        position: "topRight"
                    });
                } else if (request.status == 400) {
                    iziToast.error({
                        title: "Error",
                        message: "The stocks that you want are not available.",
                        position: "topRight"
                    });
                }
            }
        });
    }
});

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return "";
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}