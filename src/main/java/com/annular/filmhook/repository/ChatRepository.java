package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Chat;


@Repository
public interface ChatRepository extends JpaRepository<Chat,Integer> {


	@Query("SELECT c FROM Chat c WHERE (c.chatSenderId = :chatSenderId AND c.chatReceiverId = :chatReceiverId) OR (c.chatSenderId = :chatReceiverId AND c.chatReceiverId = :chatSenderId)")
	List<Chat> getMessageListBySenderIdAndReceiverId(Integer chatSenderId, Integer chatReceiverId);


    @Query("SELECT c FROM Chat c WHERE (c.chatSenderId = :senderId AND c.chatReceiverId = :receiverId) " +
           "OR (c.chatSenderId = :receiverId AND c.chatReceiverId = :senderId) ORDER BY c.timeStamp DESC")
    List<Chat> findTop1ByChatSenderIdAndChatReceiverIdOrderByTimeStampDesc(Integer senderId, Integer receiverId);

	

}
