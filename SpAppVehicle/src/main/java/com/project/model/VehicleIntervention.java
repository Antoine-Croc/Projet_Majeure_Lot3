package com.project.model;

import java.util.ArrayList;
import java.util.List;

public class VehicleIntervention
{
	public List<InterventionDto> listIntervention;
	
    /** Constructeur privé */
    private VehicleIntervention()
    {
    	this.listIntervention = new ArrayList<InterventionDto>();
    }
     
    /** Instance unique non préinitialisée */
    private static VehicleIntervention INSTANCE = null;
     
    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized VehicleIntervention getInstance()
    {           
        if (INSTANCE == null)
        {   INSTANCE = new VehicleIntervention(); 
        }
        return INSTANCE;
    }
    
    
    
}