function station_filter() {
    station_markerDelAgain()
    const GET_URL="http://localhost:8080/stations/"; 
    let context =   {
        method: 'GET'
    };    
    
    fetch(GET_URL,context)
        .then(response => response.json())
            .then(response => callback_station_update(response))
            .catch(error => err_callback(error));
}

function callback_station_update(response){
    for (let i = 0; i < response.length; i++) {
        var station = L.marker([response[i]["coord"]["lat"],response[i]["coord"]["lon"]],{
            icon: stationIcon,
        });
        //mettre en place les conditons de filtrage
        station.addTo(map)
        .bindPopup('Caserne '+response[i]["id"]);
    station_marker.push(station);
    }
}

function err_callback(error){
    console.log(error);
}

function station_markerDelAgain() {
    for(i=0;i<station_marker.length;i++) {
        map.removeLayer(station_marker[i]);
    }
}


function intervalleStation() {
    setInterval(function(){ station_filter(); }, 1000);
  }