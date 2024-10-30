package com.annular.filmhook.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Chat;
import com.annular.filmhook.model.MarketPlace;
import com.annular.filmhook.model.MarketPlaceChat;

@Repository
public interface MarketPlaceChatRepository extends JpaRepository<MarketPlaceChat,Integer>{


	boolean existsByMarketPlaceSenderIdAndMarketPlaceReceiverId(Integer marketPlaceSenderId, Integer marketPlaceReceiverId);

	boolean existsByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndAcceptTrue(Integer marketPlaceSenderId, Integer marketPlaceReceiverId);

	List<MarketPlaceChat> getMessageListByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndMarketType(Integer senderId,
			Integer receiverId, String marketType);



	@Query("SELECT DISTINCT c.marketPlaceSenderId FROM MarketPlaceChat c WHERE c.marketPlaceReceiverId = :loggedInUserId AND c.marketType = :marketType")
	Set<Integer> findSenderIdsByReceiverIdAndMarketType(Integer loggedInUserId, String marketType);

	@Query("SELECT DISTINCT c.marketPlaceReceiverId FROM MarketPlaceChat c WHERE c.marketPlaceSenderId = :loggedInUserId AND c.marketType = :marketType")
	Set<Integer> findReceiverIdsBySenderIdAndMarketType(Integer loggedInUserId, String marketType);
//
//
//
//	 @Query("SELECT m FROM MarketPlaceChat m " +
//	           "WHERE m.marketPlaceSenderId = :senderId " +
//	           "AND m.marketPlaceReceiverId = :receiverId " +
//	           "AND m.marketType = :marketType " +
//	           "ORDER BY m.timeStamp DESC")
//	Optional<MarketPlaceChat> findLatestMessageByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndMarketType(
//			Integer senderId, Integer receiverId, String marketType);
//	@Query("SELECT c FROM MarketPlaceChat c WHERE ((c.marketPlaceSenderId = :receiverId AND c.marketPlaceReceiverId = :senderId) OR (c.marketPlaceSenderId = :senderId AND c.marketPlaceReceiverId = :receiverId)) ORDER BY c.timeStamp DESC")
//	List<MarketPlaceChat> findLatestMessages(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

	@Query("SELECT c FROM MarketPlaceChat c WHERE ((c.marketPlaceSenderId = :receiverId AND c.marketPlaceReceiverId = :senderId) OR (c.marketPlaceSenderId = :senderId AND c.marketPlaceReceiverId = :receiverId)) AND c.marketType = :marketType ORDER BY c.timeStamp DESC")
	List<MarketPlaceChat> findLatestMessages(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId, @Param("marketType") String marketType);

	@Query(value = "SELECT c FROM MarketPlaceChat c WHERE ((c.marketPlaceSenderId = :loggedInUserId AND c.marketPlaceReceiverId = :userId) OR (c.marketPlaceSenderId = :userId AND c.marketPlaceReceiverId = :loggedInUserId)) AND c.marketType = :marketType ORDER BY c.timeStamp DESC")
	List<MarketPlaceChat> getLatestMessage(@Param("loggedInUserId") Integer loggedInUserId, @Param("userId") Integer userId, @Param("marketType") String marketType);

	@Query("SELECT COUNT(m) > 0 FROM MarketPlaceChat m WHERE m.marketType = :marketType")
	boolean marketTypeExists(String marketType);

    @Query("SELECT c FROM MarketPlaceChat c WHERE c.marketPlaceChatId = :id")
	Optional<MarketPlaceChat> findByIds(Integer id);



	

}
