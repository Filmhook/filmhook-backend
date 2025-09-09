package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annular.filmhook.exception.ResourceNotFoundException;
import com.annular.filmhook.model.AuditionCartItems;
import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.AuditionCartItemsRepository;
import com.annular.filmhook.repository.FilmProfessionRepository;
import com.annular.filmhook.repository.FilmSubProfessionRepository;
import com.annular.filmhook.repository.MovieCategoryRepository;
import com.annular.filmhook.repository.MovieSubCategoryRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.util.S3Util;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.FilmProfessionResponseDTO;
import com.annular.filmhook.webmodel.FilmSubProfessionResponseDTO;
@Service
public class AuditionNewServiceImpl implements AuditionNewService {

	@Autowired
	MovieCategoryRepository categoryRepo;
	@Autowired
	MovieSubCategoryRepository subCategoryRepo;
	@Autowired
	private FilmSubProfessionRepository filmSubProfessionRepository;
	@Autowired
	S3Util s3Util;
	@Autowired
	private AuditionCartItemsRepository auditionCartItemsRepository;
	 @Autowired
	    UserRepository userRepository;
	 @Autowired
	 private FilmProfessionRepository filmProfessionRepository;


	public List<MovieCategory> getAllCategories() {
		return categoryRepo.findAll();
	}


	public List<MovieSubCategory> getSubCategories(Integer categoryId) {
		return subCategoryRepo.findByCategoryId(categoryId);
	}

	@Override
	public List<FilmSubProfessionResponseDTO> getAllSubProfessions() {
		return filmSubProfessionRepository.findAll()
				.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<FilmSubProfessionResponseDTO> getSubProfessionsByProfessionId(Integer professionId) {
		FilmProfession profession = new FilmProfession();
		profession.setFilmProfessionId(professionId);

		return filmSubProfessionRepository.findByProfession(profession)
				.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	private FilmSubProfessionResponseDTO mapToDTO(FilmSubProfession sub) {
		return FilmSubProfessionResponseDTO.builder()
				.id(sub.getSubProfessionId())
				.subProfessionName(sub.getSubProfessionName())
				.professionName(sub.getProfession().getProfessionName())
				.filmProfessionId(sub.getProfession().getFilmProfessionId())
				.iconFilePath(  !Utility.isNullOrBlankWithTrim(sub.getProfession().getFilePath()) 
						? s3Util.generateS3FilePath(sub.getProfession().getFilePath()) 
								: "")
				.shortCharacters(generateShortCharacters(sub.getProfession().getProfessionName()))
				.build();
	}

	private String generateShortCharacters(String professionName) {
		if (professionName == null || professionName.isEmpty()) return "";
		return professionName.length() <= 3
				? professionName.toUpperCase()
						: professionName.substring(0, 3).toUpperCase();
	}
	
	@Override
	public void addToCart(Integer userId, Integer companyId, Integer subProfessionId, Integer count) {
	    FilmSubProfession subProfession = filmSubProfessionRepository.findById(subProfessionId)
	            .orElseThrow(() -> new ResourceNotFoundException("SubProfession not found with id: " + subProfessionId));

	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

	    AuditionCartItems existingItem = auditionCartItemsRepository
	            .findByUserAndCompanyIdAndSubProfession(user, companyId, subProfession)
	            .orElse(null);

	    if (existingItem != null) {
	        existingItem.setCount(existingItem.getCount() + count);
	        auditionCartItemsRepository.save(existingItem);
	    } else {
	        AuditionCartItems cartItem = AuditionCartItems.builder()
	                .user(user)
	                .companyId(companyId)
	                .subProfession(subProfession)
	                .count(count)
	                .build();
	        auditionCartItemsRepository.save(cartItem);
	    }
	}



	@Override
	public List<FilmSubProfessionResponseDTO> getCart(Integer userId, Integer companyId) {
	    // ✅ Check user from DB
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

	    // ✅ Fetch cart items for user + company
	    List<AuditionCartItems> cartItems = auditionCartItemsRepository.findByUserAndCompanyId(user, companyId);

	    // (Optional) handle empty cart gracefully
	    if (cartItems.isEmpty()) {
	        throw new ResourceNotFoundException("No cart items found for user " + userId + " and company " + companyId);
	    }

	    return cartItems.stream()
	            .map(item -> FilmSubProfessionResponseDTO.builder()
	                    .id(item.getSubProfession().getSubProfessionId())
	                    .subProfessionName(item.getSubProfession().getSubProfessionName())
	                    .professionName(item.getSubProfession().getProfession().getProfessionName())
	                    .filmProfessionId(item.getSubProfession().getProfession().getFilmProfessionId())
	                    .iconFilePath(
	                            !Utility.isNullOrBlankWithTrim(item.getSubProfession().getProfession().getFilePath())
	                                    ? s3Util.generateS3FilePath(item.getSubProfession().getProfession().getFilePath())
	                                    : ""
	                    )
	                    .shortCharacters(generateShortCharacters(item.getSubProfession().getProfession().getProfessionName()))
	                    .count(item.getCount())
	                    .build()
	            )
	            .collect(Collectors.toList());
	}
	
	@Override
	public List<FilmProfessionResponseDTO> getAllProfessions() {
	    return filmProfessionRepository.findAll()
	            .stream()
	            .map(profession -> {
	                // sum all counts of subProfessions for this profession
	                long totalCount = auditionCartItemsRepository
	                        .findAll()
	                        .stream()
	                        .filter(item -> item.getSubProfession().getProfession().getFilmProfessionId()
	                                .equals(profession.getFilmProfessionId()))
	                        .mapToLong(item -> item.getCount() != null ? item.getCount() : 0)
	                        .sum();

	                return FilmProfessionResponseDTO.builder()
	                        .id(profession.getFilmProfessionId())
	                        .professionName(profession.getProfessionName())
	                        .iconFilePath(
	                                !Utility.isNullOrBlankWithTrim(profession.getFilePath())
	                                        ? s3Util.generateS3FilePath(profession.getFilePath())
	                                        : ""
	                        )
	                        .count(totalCount)
	                        .build();
	            })
	            .collect(Collectors.toList());
	}
	
	





}
