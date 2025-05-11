package cfs.kalah.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/sample")
public class SampleController {
	private static final Logger LOG = LoggerFactory.getLogger(SampleController.class);
	
	/**
	 * sample get
	 * 
	 * Trying out a simple GET after my failed codility test of 17/10/2024 for TSB
	 * 
	 * Usage: 
	 * 1. Run CFSKalahApplication main block
	 * 2. curl -v http://localhost:8080/sample/getSomething
	 */
	@RequestMapping(path = "getSomething", method = GET)
	public Map<String, Integer> getSomething() {

		Map<String, Integer> result = new HashMap<>();
		result.put("total", 123);
		
		return result;
	}

}
