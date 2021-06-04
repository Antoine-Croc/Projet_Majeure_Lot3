package com.project.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.project.model.dto.LiquidType;
import com.project.model.dto.VehicleDto;
import com.project.model.dto.VehicleType;

@Entity
public class Vehicle  {

	public static final int CREW_MEMBER_START_VALUE=-1;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private Integer idDto;
	private double lon;
	private double lat;
	private VehicleType type;
	private float efficiency; // need all crew member to reach full efficiency value between 0 and 10
	private LiquidType liquidType; // type of liquid effective to type of fire
	private float liquidQuantity; // total quantity of liquid
	private float liquidConsumption; // per second when use
	private float fuel;		// total quantity of fuel
	private float fuelConsumption; // per km
	private int crewMember;
	private int crewMemberCapacity;
	private Integer facilityRefID;
	private boolean isIntervention = false;
	


	public Vehicle() {
		crewMember= CREW_MEMBER_START_VALUE;
		liquidType=LiquidType.ALL;
	}

	public Vehicle(VehicleDto vehicleDto) {
		super();
		this.idDto= 0;
		this.lon = vehicleDto.getLon();
		this.lat = vehicleDto.getLat();
		this.type = vehicleDto.getType();
		this.efficiency = vehicleDto.getEfficiency();
		this.liquidType = vehicleDto.getLiquidType();
		this.liquidQuantity = vehicleDto.getLiquidQuantity();
		this.liquidConsumption = vehicleDto.getLiquidConsumption();
		this.fuel = vehicleDto.getFuel();
		this.fuelConsumption = vehicleDto.getFuelConsumption();
		this.crewMember = vehicleDto.getCrewMember();
		this.crewMemberCapacity = vehicleDto.getCrewMemberCapacity();
		this.facilityRefID = vehicleDto.getFacilityRefID();
	}

	@Override
	public String toString() {
		return "[vehicle:"+id+", ["+lat+", "+lon+"], "+"[idDto:"+idDto+"] " +"]";
	}
	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public float getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(float efficiency) {
		this.efficiency = efficiency;
	}

	public LiquidType getLiquidType() {
		return liquidType;
	}

	public void setLiquidType(LiquidType liquidType) {
		this.liquidType = liquidType;
	}

	public float getLiquidQuantity() {
		return liquidQuantity;
	}

	public void setLiquidQuantity(float liquidQuantity) {
		this.liquidQuantity = liquidQuantity;
	}

	public float getLiquidConsumption() {
		return liquidConsumption;
	}

	public void setLiquidConsumption(float liquidConsumption) {
		this.liquidConsumption = liquidConsumption;
	}

	public float getFuel() {
		return fuel;
	}

	public void setFuel(float fuel) {
		this.fuel = fuel;
	}

	public float getFuelConsumption() {
		return fuelConsumption;
	}

	public void setFuelConsumption(float fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}

	public int getCrewMember() {
		return crewMember;
	}

	public void setCrewMember(int crewMember) {
		this.crewMember = crewMember;
	}

	public int getCrewMemberCapacity() {
		return crewMemberCapacity;
	}

	public void setCrewMemberCapacity(int crewMemberCapacity) {
		this.crewMemberCapacity = crewMemberCapacity;
	}

	public Integer getFacilityRefID() {
		return facilityRefID;
	}

	public void setFacilityRefID(Integer facilityRefID) {
		this.facilityRefID = facilityRefID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdDto() {
		return idDto;
	}

	public void setIdDto(Integer idDto) {
		this.idDto = idDto;
	}
	
	public boolean isIntervention() {
		return isIntervention;
	}

	public void setIntervention(boolean isIntervention) {
		this.isIntervention = isIntervention;
	}
	

}
