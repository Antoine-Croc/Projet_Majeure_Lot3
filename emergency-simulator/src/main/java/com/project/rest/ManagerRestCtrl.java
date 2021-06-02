package com.project.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.Manager;
import com.project.model.dto.Coord;


@RestController
public class ManagerRestCtrl {

	@RequestMapping("/hello")
	public String sayHello() {
		return "Hello Manager !!!";
	}
	@RequestMapping(method=RequestMethod.POST,value="/addManager",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public void addManager(@RequestBody Coord coord) {
///		Manager man = new Manager(coord);
		System.out.println("start-----------"+coord);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/msg/{id1}/{id2}")
	public String getMsg(@PathVariable String id1, @PathVariable String id2) {
		String msg1=id1;
		String msg2=id2;
		return "Composed Message: msg1:"+msg1+"msg2:"+msg2;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/parameters")
	public String getInfoParam(@RequestParam String param1,@RequestParam String param2) {
		return "Parameters: param1:"+param1+"param2:"+param2;
	}


}
