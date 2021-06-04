package com.sp.rest;

public class FireRestCtrl {

	@Autowired
	FireService fService;
	
	@RequestMapping(method=RequestMethod.POST,value="/fires")
	public void addFire(@RequestParam)
}
