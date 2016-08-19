new Vue({
    el: '#app',
    data: {
        stations: []
    },
    methods: {
        updateData: function () {
            this.$http.get('api.php/stations').then(function (response, status, request) {
                this.stations = JSON.parse(response.body);
            }).finally(function () {
                setTimeout(this.updateData, 2000);
            });
        }
    },
    ready: function() {
        this.updateData();
    }
});

Vue.filter('prettydate', function (unix_timestamp) {
    var seconds_since = Math.floor(Date.now() / 1000) - unix_timestamp;
    if (seconds_since < 10){
        return 'Jetzt';
    }
    if (seconds_since < 60){
        return 'Vor ' + (Math.round(seconds_since/5) * 5) + ' Sekunden';
    }

    var date = new Date(unix_timestamp*1000);
    // Hours part from the timestamp
    var hours = date.getHours();
    // Minutes part from the timestamp
    var minutes = "0" + date.getMinutes();
    // Seconds part from the timestamp
    var seconds = "0" + date.getSeconds();

    // Will display time in 10:30:23 format
    return hours + ':' + minutes.substr(-2);
});