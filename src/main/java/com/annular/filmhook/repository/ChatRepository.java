package com.annular.filmhook.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Query(value = "SELECT * FROM chat c WHERE c.chat_sender_id=:senderId AND c.chat_receiver_id=:receiverId ORDER BY c.time_stamp DESC LIMIT 1;", nativeQuery = true)
    Optional<Chat> getLatestMessage(Integer senderId, Integer receiverId);

    @Query("SELECT DISTINCT c.chatSenderId FROM Chat c WHERE c.chatReceiverId = :loggedInUserId")
    Set<Integer> findSenderIdsByReceiverId(Integer loggedInUserId);

    @Query("SELECT DISTINCT c.chatReceiverId FROM Chat c WHERE c.chatSenderId = :loggedInUserId")
    Set<Integer> findReceiverIdsBySenderId(Integer loggedInUserId);
}
