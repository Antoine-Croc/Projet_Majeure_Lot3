function modifVehicule() {
    var id = document.getElementById("id_cam").value
    let GET_URL = 'http://localhost:8080/vehicles/' + id;
        let context =   {
            method: 'GET'
        };    
        
        fetch(GET_URL,context)
            .then(response => response.json())
                .then(response => maj_modif_vehicle(response))
                .catch(error => err_callback(error));
}

function maj_modif_vehicle(response){
    response["type"]= document.getElementById("type").value;
    response["liquidType"]= document.getElementById("liquid").value;
    response["lat"]= document.getElementById("lat").value;
    response["lon"]= document.getElementById("lon").value;
    response["fuel"]= document.getElementById("fuel").value;
    response["facilityRefID"]= document.getElementById("facilityRefID").value;
    response["crewMemberCapacity"]= document.getElementById("crewMemberCapacity").value;
	let url = 'http://localhost:8080/vehicles/' + response["id"];
	fetch(url,{
	  headers: {
	    'Content-Type': 'application/json'
	  },
	  method: "PUT",
	  body: JSON.stringify(response)
    }).then(response => vehicle_filter());
}

function creatVehicule(lat,lon) {
	let params = {
			"lon": lon,
			"lat": lat,
			"type": "TRUCK",
			"efficiency": 10.0,
			"liquidType": "ALL",
			"liquidQuantity": 100.0,
			"liquidConsumption": 1.0,
			"fuel": 100.0,
			"fuelConsumption": 10.0,
			"crewMember": 8,
			"crewMemberCapacity": 8,
			"facilityRefID": 0
    };
    
	let url = 'http://localhost:8080/vehicles/';
	
	fetch(url,{
	  headers: {

	    'Content-Type': 'application/json'
	  },
	  method: "POST",
	  body: JSON.stringify(params)
    }).then(response => vehicle_filter());
}

function deletVehicule(id) {    
	let url = 'http://localhost:8080/vehicles/'+id;
	
	fetch(url,{
	  headers: {

	    'Content-Type': 'application/json'
	  },
	  method: "DELETE"
    }).then(response => vehicle_filter());
}

function vehicle_filter() {
    vehicle_markerDelAgain()
    document.getElementById("valueCapacity").innerHTML = document.getElementById("capacity").value
    const GET_URL="http://localhost:8080/vehicles/"; 
    let context =   {
        method: 'GET'
    };    
    
    fetch(GET_URL,context)
        .then(response => response.json())
            .then(response => callback_vehicle_update(response))
            .catch(error => err_callback(error));
}

function callback_vehicle_update(response){
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
        if ((document.getElementById("CAR").checked == true && response[i]["type"]=="CAR") || 
            (document.getElementById("FIRE_ENGINE").checked == true && response[i]["type"]=="FIRE_ENGINE") || 
            (document.getElementById("PUMPER_TRUCK").checked == true && response[i]["type"]=="PUMPER_TRUCK")|| 
            (document.getElementById("WATER_TENDER").checked == true && response[i]["type"]=="WATER_TENDER") || 
            (document.getElementById("TURNTABLE_LADDER_TRUCK").checked == true && response[i]["type"]=="TURNTABLE_LADDER_TRUCK") ||
            (document.getElementById("TRUCK").checked == true && response[i]["type"]=="TRUCK") ||
            document.getElementById("ALL_FIRE_CARS").checked == true) {
            if ((document.getElementById("WATER").checked == true && response[i]["liquidType"]=="WATER") ||
             (document.getElementById("WATER_OR_FOAM").checked == true && response[i]["liquidType"]=="WATER_OR_FOAM") || 
             (document.getElementById("CO2").checked == true && response[i]["liquidType"]=="CO2")|| 
             (document.getElementById("POWDER").checked == true && response[i]["liquidType"]=="POWDER") || 
             document.getElementById("ALL_FIRE_EXTINGUISHER").checked == true){
                if (document.getElementById("capacity").value <= response[i]["crewMemberCapacity"]) {
                    if (!id_moving_vehicle.includes(response[i]["id"])){
                        vehicle.addTo(map)
                            .bindPopup(template);
                        vehicle_marker.push(vehicle);
                    }
                }
            }
        }
    }
}

function vehicle_markerDelAgain() {
    for(i=0;i<vehicle_marker.length;i++) {
        map.removeLayer(vehicle_marker[i]);
    }
    vehicle_marker.splice(0,vehicle_marker.length)
}


function err_callback(error){
    console.log(error);
}

function intervention_vehicule(id){
    intervention();
}