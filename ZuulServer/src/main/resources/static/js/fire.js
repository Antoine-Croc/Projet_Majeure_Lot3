function fire_filter() {
    fire_markerDelAgain()
    document.getElementById("valueIntensity").innerHTML = document.getElementById("intensity").value
    document.getElementById("valueRange").innerHTML = document.getElementById("range").value
    const GET_URL="http://localhost:8080/fires/"; 
    let context =   {
        method: 'GET'
    };    
    
    fetch(GET_URL,context)
        .then(response => response.json())
            .then(response => callback_deux(response))
            .catch(error => err_callback(error));
}

function callback_deux(response){
    for (let i = 0; i < response.length; i++) {
        var circle = L.circle([response[i]["lat"],response[i]["lon"]], 
        response[i]["range"],{
            color: 'red',
            fillColor: '#f03',
            fillOpacity: response[i]["intensity"]/100,
        });
        if ((document.getElementById("A").checked == true && response[i]["type"]=="A") ||
        (document.getElementById("B_Gasoline").checked == true && response[i]["type"]=="B_Gasoline") || 
        (document.getElementById("B_Alcohol").checked == true && response[i]["type"]=="B_Alcohol") ||
        (document.getElementById("B_Plastics").checked == true && response[i]["type"]=="B_Plastics")||
        (document.getElementById("C_Flammable_Gases").checked == true && response[i]["type"]=="C_Flammable_Gases") || 
        (document.getElementById("D_Metals").checked == true && response[i]["type"]=="D_Metals") || 
        (document.getElementById("E_Electric").checked == true && response[i]["type"]=="E_Electric") || 
        document.getElementById("all").checked == true) {    
            if (document.getElementById("intensity").value <= response[i]["intensity"] && document.getElementById("range").value <= response[i]["range"]) {
                circle.addTo(map)
                .bindPopup('Je suis le feu '+response[i]["id"]+': <ul><li>Intensité : '+response[i]["intensity"]+'</li><li>Type : '+response[i]["type"]+'</li></ul>\
                    ');
                fire_marker.push(circle);
            }
        }
    }
}

function err_callback(error){
    console.log(error);
}

function fire_markerDelAgain() {
    for(i=0;i<fire_marker.length;i++) {
        map.removeLayer(fire_marker[i]);
    }
}

function intervalleFire() {
    setInterval(function(){ fire_filter(); }, 30000);
  }