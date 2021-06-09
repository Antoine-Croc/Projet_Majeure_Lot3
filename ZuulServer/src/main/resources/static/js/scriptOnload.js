
var popup = L.popup();

var vehicleIcon = L.icon({
    iconUrl: 'images/firecar.png',
    iconSize:     [25, 25]
});

var stationIcon = L.icon({
    iconUrl: 'images/firestation.png',
    iconSize:     [25, 25]
});

function onMapClick(e) {
    popup
        .setLatLng(e.latlng)
        .setContent("Voulez-vous créer un véhicule ici : <input type='submit' id='submit' value='oui' onclick='creatVehicule("+ e.latlng.lat+"," + e.latlng.lng+")'/>")
        .openOn(map);
}

map.on('click', onMapClick);
const FIRE_URL="http://localhost:8080/fires/";
const VEHICLE_URL= "http://localhost:8080/vehicles/";
const STATION_URL = "http://localhost:8080/stations/"
let context =   {
method: 'GET'
};  

fetch(FIRE_URL,context)
.then(response_fire => response_fire.json())
    .then(response_fire => callback_fire(response_fire))
    .catch(error => err_callback(error));

fetch(VEHICLE_URL,context)
.then(response_vehicle => response_vehicle.json())
    .then(response_vehicle => callback_vehicle(response_vehicle))
    .catch(error => err_callback(error));

fetch(STATION_URL,context)
.then(response_station => response_station.json())
    .then(response_station => callback_station(response_station))
    .catch(error => err_callback(error));

function callback_fire(response){
    for (let i = 0; i < response.length; i++) {
        var circle = L.circle([response[i]["lat"],response[i]["lon"]], 
        response[i]["range"],{
            color: 'red',
            fillColor: '#f03',
            fillOpacity: response[i]["intensity"]/100,
        });
   
    circle.addTo(map)
        .bindPopup('Je suis le feu '+response[i]["id"]+': <ul><li>Intensité : '+response[i]["intensity"]+'</li><li>Type : '+response[i]["type"]+'</li></ul>\
        ');
    fire_marker.push(circle);
    }
}

function change_vehicle_name(response){
    switch(response["type"]){
        case "CAR": return "Voiture";
        case "TRUCK": return "Camion";
        case "PUMPER_TRUCK": return "fourgons-pompes";
        case "WATER_TENDER": return "camions citernes";
        case "FIRE_ENGINE": return "véhicules de secours";
        case "TURNTABLE_LADDER_TRUCK": return "camions-échelles";
        default : return "Undefined";
    }
}

function change_liquid_name(response){
    switch(response["liquidType"]){
        case "WATER": return "Eau";
        case "WATER_OR_FOAM": return "Eau avec additif ou mousse";
        case "CO2": return "Dioxyde de carbone";
        case "POWDER": return "Poudre";
        default : return "Undefined";
    }
}

function callback_vehicle(response){
    
    for (let i = 0; i < response.length; i++) {
        var vehicle = L.marker([response[i]["lat"],response[i]["lon"]],{
            icon: vehicleIcon,
        });
        var template = '\
        <div>\
        <p style="color:black;">Le véhicule '+response[i]["id"]+':</p>\
            <table class="popup-table">\
                <tr class="popup-table-row">\
                <th class="popup-table-header">Type:</th>\
                <td id="value-arc" class="popup-table-data">'+change_vehicle_name(response[i])+'</td>\
                </tr>\
                <tr class="popup-table-row">\
                <th class="popup-table-header">Type de liquide:</th>\
                <td id="value-speed" class="popup-table-data">'+change_liquid_name(response[i])+'</td>\
                </tr>\
                <tr class="popup-table-row">\
                <th class="popup-table-header">Quantité de liquide:</th>\
                <td id="value-speed" class="popup-table-data">'+response[i]["liquidQuantity"]+'</td>\
                </tr>\
                <tr class="popup-table-row">\
                <th class="popup-table-header">Quantité de fuel:</th>\
                <td id="value-speed" class="popup-table-data">'+response[i]["fuel"]+'</td>\
                </tr>\
            </table></br><p style="color:black;">Modifier les paramètres du véhicule :</p>\
            <input type="hidden" id="id_cam"  value='+response[i]["id"]+'>\
            <input type="hidden" id="lon"  value='+response[i]["lon"]+'>\
            <input type="hidden" id="lat"  value='+response[i]["lat"]+'>\
            <input type="hidden" id="fuel"  value='+response[i]["fuel"]+'>\
            <input type="hidden" id="facilityRefID"  value='+response[i]["facilityRefID"]+'>\
            <input type="hidden" id="crewMemberCapacity"  value='+response[i]["crewMemberCapacity"]+'>\
            <input type="hidden" id="crewMember"  value='+response[i]["crewMember"]+'>\
            <label style="color:black;" for="liquid">Type de liquide:</label>\
            <select name="liquid" id="liquid">\
                <option value="">--Choisissez parmi les 2 options--</option>\
                <option value="WATER">Eau</option>\
                <option value="ALL">Agent extincteur de qualité supérieure</option>\
            </select></br> \
            <label style="color:black;" for="type">Type du véhicule:</label>\
            <select name="type" id="type">\
                <option value="">--Choisissez parmi les 6 options--</option>\
                <option value="CAR">Voiture</option>\
                <option value="FIRE_ENGINE">Véhicule de secours</option>\
                <option value="PUMPER_TRUCK">Fourgon-pompe</option>\
                <option value="WATER_TENDER">Camion citerne</option>\
                <option value="TURNTABLE_LADDER_TRUCK">Camion-échelle</option>\
                <option value="TRUCK">Camion</option>\
            </select>\
            <input type="submit" id="submit" value="Modification du véhicule" onclick="modifVehicule()">\
            <input type="submit" id="submit" value="Suppression du véhicule" onclick="deletVehicule('+response[i]["id"]+')">\
            ';
        vehicle.addTo(map)
            .bindPopup(template);
        vehicle_marker.push(vehicle);
    }
}

function callback_station(response){
    for (let i = 0; i < response.length; i++) {
        var station = L.marker([response[i]["coord"]["lat"],response[i]["coord"]["lon"]],{
            icon: stationIcon,
        });
        station.addTo(map)
        .bindPopup('Caserne '+response[i]["id"]);
    station_marker.push(station);
    }
}
