package com.annular.filmhook.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.annular.filmhook.DTO.LocationTypeDTO;
import com.annular.filmhook.model.LocationType;
import com.annular.filmhook.model.SubLocationType;
import com.annular.filmhook.model.SubLocationTypeContent;
import com.annular.filmhook.repository.LocationTypeRepository;
import com.annular.filmhook.repository.SubLocationTypeContentRepository;
import com.annular.filmhook.repository.SubLocationTypeRepository;


@RestController
@RequestMapping("/shootingLocation")
public class ShootingLocationController {
	  @Autowired
	    private LocationTypeRepository locationTypeRepository;
	  
	  @Autowired
	    private SubLocationTypeRepository subLocationTypeRepository;
	  
	  @Autowired
	    private SubLocationTypeContentRepository contentRepository;

	    @GetMapping("/getAllLocationTypes")
	    public List<LocationTypeDTO> getAllLocationTypes() {
	        List<LocationType> locationTypes = locationTypeRepository.findAll();
	        	
	        return locationTypes.stream()
	            .map(loc -> new LocationTypeDTO(loc.getId(), loc.getName())) // assuming getName() returns "Indoor"/"Outdoor"
	            .collect(Collectors.toList());
	    }
	    
	  

	    @GetMapping("/getLocationById/{id}")
	    public List<LocationTypeDTO> getLocationTypeById(@PathVariable Long id) {
	    	 Optional<LocationType> locationTypes = locationTypeRepository.findById(id);
	        return locationTypes.stream()
	                .map(loc -> new LocationTypeDTO(loc.getId(), loc.getName())) // assuming getName() returns "Indoor"/"Outdoor"
	                .collect(Collectors.toList());
	           
	    }
	    
	    @GetMapping("/getSubLocationType")
	    public List<SubLocationType> getAllSubLocationTypes() {
	        return subLocationTypeRepository.findAll();
	    }

	    @GetMapping("/getSubLocationTypeById/{id}")
	    public SubLocationType getSubLocationTypeById(@PathVariable Long id) {
	        return subLocationTypeRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("SubLocationType not found with id: " + id));
	    }

	    @GetMapping("/by-location-type/{locationTypeId}")
	    public List<SubLocationType> getByLocationTypeId(@PathVariable Long locationTypeId) {
	        return subLocationTypeRepository.findByLocationTypeId(locationTypeId);
	    }
	  

	    @GetMapping("/subLocationContent")
	    public List<SubLocationTypeContent> getAllContents() {
	        return contentRepository.findAll();
	    }

	    @GetMapping("/subLocationContent/{id}")
	    public SubLocationTypeContent getContentById(@PathVariable Long id) {
	        return contentRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));
	    }

	    @GetMapping("/by-sub-location-type/{subLocationTypeId}")
	    public List<SubLocationTypeContent> getBySubLocationType(@PathVariable Long subLocationTypeId) {
	        return contentRepository.findBySubLocationTypeId(subLocationTypeId);
	    }
}
