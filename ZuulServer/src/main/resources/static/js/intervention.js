function intervention(){
    let GET_URL = 'http://localhost:8080/vehicles/interventions';
    let context =   {
        method: 'GET'
    };
    fetch(GET_URL,context)
        .then(response => response.json())
            .then(response => maj_camion(response))
            .catch(error => err_callback(error));

}
async function maj_camion(response){  
//test move

for(let i = 0; i < response.length;i++){
    if (!(id_intervention_vehicle.includes(response[i]["vehicle"]["id"]))) {
console.log("id_intervention_vehicle"+ id_intervention_vehicle);
        id_moving_vehicle.push(response[i]["vehicle"]["id"]);
        id_intervention_vehicle.push(response[i]["vehicle"]["id"]);
        await moveByRoute(response[i]["vehicle"]["lat"],response[i]["vehicle"]["lon"],response[i]["fireLat"],response[i]["fireLon"],map,vehicleIcon,response[i]["vehicle"]["id"],response[i]["vehicle"]);
    }
}

/* 
response["lat"] = feu_interv_lat;
response["lon"] = feu_interv_lon;

let url = 'http://localhost:8080/vehicles/' + id_interv_vehicule;

fetch(url,{
headers: {
'Content-Type': 'application/json'
},
method: "PUT",
body: JSON.stringify(response)
}).then(response => vehicle_filter());
*/
vehicle_filter();
}



// bouger le vehicule
function moveByRoute(latInit,lonInit,latFinal,lonFinal,mymap,ImgIcon,idVehicle,selectedVehicle){

var promiseA =  getRoute(latInit,lonInit,latFinal,lonFinal);

promiseA.then(function(response){
    var route = response;
    moveMarqueur(mymap,ImgIcon,route,idVehicle,selectedVehicle,latFinal,lonFinal);
})
}


/*deplacement du vehicule
* cette fonction recreer le marqueur 
*/
async function moveMarqueur(mymap,ImgIcon,traffic,idVehicle,selectedVehicle,lat,lon){

var line = L.polyline(traffic);
mymap.addLayer(line);


var animatedMarker = L.animatedMarker(line.getLatLngs(),{icon:ImgIcon,
    onEnd: function() {
        let index2 = id_moving_vehicle.indexOf(idVehicle);
        if (index2 >= 0) {
            id_moving_vehicle.splice( index2, 1 );
        }

        vehicle_filter();
        mymap.removeLayer(line);
        mymap.removeLayer(animatedMarker);
        //update position à la fin
        let lat_fire_final = traffic[traffic.length-1][0];
        let lon_fire_final = traffic[traffic.length-1][1];
        
        updatePosition(selectedVehicle,lat_fire_final,lon_fire_final);
        //check fire intensity 
        CheckFireIntensityIntervalle(idVehicle,lat,lon)
        
        },
        vehicle : selectedVehicle,

    });

mymap.addLayer(animatedMarker);
//ajouter  le  AnimatedMarker  dans la liste
// vehicle_marker.push(animatedMarker);

}

//Get l'itineraire (un tableau)
function getRoute(latInit,lonInit,latFinal,lonFinal){

var myHeaders = new Headers();
var port = 8080;
var myInit = {
    method: 'GET',
    headers: myHeaders,
    mode: 'cors',
    cache: 'default'
};

var url = 'http://localhost:' + port + '/route/'+latInit+"/"+lonInit+"/"+latFinal+"/"+lonFinal;
const myResponse =  fetch(url, myInit)
    .then(function (response) {
        return  response.json();
    })
    .then((data) => {
      return  data ;

    })
    .catch((err) => {
        console.log(" not get  Route Info");
    });

return myResponse;

}

function interventionIntervalle(){
setInterval(function(){ intervention(); }, 2000);
}


var intervalle;
var IntervalFonctionList = Array();
function CheckFireIntensityIntervalle(idVehicle,lat,lon) {
  let idFonction = setInterval(function(){ CheckFireIntensity(idVehicle,lat,lon); }, 3000);
 pushElement(IntervalFonctionList,idFonction,idVehicle);

}

function CheckFireIntensity(idVehicle,lat,lon){
var myHeaders = new Headers();
var port = 8080;
var myInit = {
    method: 'GET',
    headers: myHeaders,
    mode: 'cors',
    cache: 'default'
};
var url = 'http://localhost:' + port + '/fires/intensity?lat='+lat+"&lon="+lon;
const myResponse =  fetch(url, myInit)
    .then(function (response) {
        return  response.text();
    })
    .then((data) => {

        let intensity = parseFloat(data);
        console.log("Fire at ["+lat+","+lon+"], "+" Intensity  = "+intensity);
        if( intensity <= 0.1){
            let index1 = id_intervention_vehicle.indexOf(idVehicle);
          if (index1 >= 0) {
                id_intervention_vehicle.splice( index1, 1 );
            }

            //suprimer la setIntervalle
            clearSetInterval(IntervalFonctionList,idVehicle);
        }
        return  intensity ;
    });
}

async function clearSetInterval(list,idV){
    for await(let ele of list){
        if(ele[0] == idV){
            window.clearInterval(ele[1]);

        }

    }
}
 function pushElement(list,idF,idV){
     let result = false;
        for (let pas = 0; pas < list.length; pas++){
            if(list[pas][0] == idV){
                result=true;
                list[pas][1]=idF;

            }
    
        }
        if(result == false){
            list.push([idV,idF]);
        }



}