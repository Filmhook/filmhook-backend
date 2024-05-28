package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.service.FilmProfessionService;
import com.annular.filmhook.webmodel.FilmWebModel;

@Service
public class FilmProfessionServiceImpl implements FilmProfessionService {

    @Autowired
    FilmProfessionRepository filmProfessionRepository;

    @Override
    public ResponseEntity<?> getProfessionList(FilmWebModel filmWebModel) {
        try {
			FilmProfession filmProfession = filmProfessionRepository.findById(filmWebModel.getFilmProfesssionId()).orElse(null);
			if (filmProfession != null) {
				List<String> subProfessionNames = filmProfession.getFilmSubProfessionCollection().stream()
						.map(FilmSubProfession::getSubProfessionName)
						.collect(Collectors.toList());
				// Create a map with the desired structure
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("subProfessionName", subProfessionNames);
				return ResponseEntity.ok(responseMap);
			} else {
				// FilmProfession entity not found for the given filmProfessionId
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Failed to fetch professions");
		}
    }

    public ResponseEntity<?> getProfessionMapList(FilmWebModel filmWebModel) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> professionList = new ArrayList<>();
        try {
            List<FilmProfession> professions = filmProfessionRepository.findAll();
            for (FilmProfession profession : professions) {
                Map<String, Object> professionMap = new HashMap<>();
                professionMap.put("filmProfessionId", profession.getFilmProfessionId());
                professionMap.put("professionName", profession.getProfessionName());
                //professionMap.put("professionImage", Base64.getEncoder().encode(profession.getImage()));
                professionList.add(professionMap);
            }
            response.put("professionMapList", professionList);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch professions");
        }
    }

}