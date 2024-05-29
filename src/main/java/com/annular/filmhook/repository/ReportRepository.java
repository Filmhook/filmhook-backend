package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ReportPost;


@Repository
public interface ReportRepository extends JpaRepository<ReportPost,Integer> {

	List<ReportPost> findByPostId(Integer postId);

}
