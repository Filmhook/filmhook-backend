package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PostTags;

@Repository
public interface PostTagsRepository extends JpaRepository<PostTags, Integer> {

	
	List<PostTags> findByTaggedUserUserIdAndStatusTrue(Integer userId);

}
