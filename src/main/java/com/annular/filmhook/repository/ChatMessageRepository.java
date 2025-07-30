package com.annular.filmhook.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Chat;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<Chat, Integer> {

    @Query("SELECT DISTINCT c.chatReceiverId " +
           "FROM Chat c " +
           "WHERE c.chatSenderId = :userId " +
           "ORDER BY c.chatCreatedOn DESC")
    List<Integer> findRecentChatUserIdsByUserId(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT c.chatSenderId " +
           "FROM Chat c " +
           "WHERE c.chatReceiverId = :userId " +
           "ORDER BY c.chatCreatedOn DESC")
    List<Integer> findUsersWhoMessagedMe(@Param("userId") Integer userId);
    @Query(value = """
    	    SELECT 
    	        CASE 
    	            WHEN chat_sender_id = :userId THEN chat_receiver_id 
    	            ELSE chat_sender_id 
    	        END AS partner_id
    	    FROM chat
    	    WHERE chat_sender_id = :userId OR chat_receiver_id = :userId
    	    GROUP BY partner_id
    	    ORDER BY MAX(chat_created_on) DESC
    	    """, nativeQuery = true)
    	List<Integer> findRecentChatPartnerIds(@Param("userId") Integer userId);
}
