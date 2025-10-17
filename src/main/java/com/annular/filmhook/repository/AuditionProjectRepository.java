	package com.annular.filmhook.repository;
	
	import java.time.LocalDateTime;
	import java.util.List;
	import java.util.Optional;
	
	import javax.transaction.Transactional;
	
	import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.data.jpa.repository.Modifying;
	import org.springframework.data.jpa.repository.Query;
	import org.springframework.data.repository.query.Param;
	import org.springframework.stereotype.Repository;
	
	import com.annular.filmhook.model.AuditionNewProject;
	
	@Repository
	public interface AuditionProjectRepository extends JpaRepository<AuditionNewProject, Integer> {
		
	//	List<AuditionNewProject> findAllByCompanyId(Integer companyId);
		
		List<AuditionNewProject> findAllByCompanyIdAndIsDeletedFalseOrderByIdDesc(Integer companyId);
	
		 Optional<AuditionNewProject> findByIdAndIsDeletedFalse(Integer id);
		 
		 @Modifying
		    @Transactional
		    @Query("UPDATE AuditionNewProject a SET a.status = false WHERE a.createdBy = :userId AND a.status = true")
		    void deactivateByCreatedBy(@Param("userId") Integer userId);
		 
		 @Modifying
		 @Transactional
		 @Query("UPDATE AuditionNewProject a SET a.status = false, a.isDeleted = true, a.deletedOn = CURRENT_TIMESTAMP, a.deletedBy = :deletedBy WHERE a.createdBy = :createdBy AND a.isDeleted = false")
		 void deactivateByCreatedBy(@Param("createdBy") Integer createdBy, @Param("deletedBy") Integer deletedBy);




	
	
	}