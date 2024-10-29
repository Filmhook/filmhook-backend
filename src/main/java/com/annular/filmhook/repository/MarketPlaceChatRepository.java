package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.MarketPlaceChat;

@Repository
public interface MarketPlaceChatRepository extends JpaRepository<MarketPlaceChat,Integer>{


	boolean existsByMarketPlaceSenderIdAndMarketPlaceReceiverId(Integer marketPlaceSenderId, Integer marketPlaceReceiverId);

	boolean existsByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndAcceptTrue(Integer marketPlaceSenderId, Integer marketPlaceReceiverId);

	List<MarketPlaceChat> getMessageListByMarketPlaceSenderIdAndMarketPlaceReceiverIdAndMarketType(Integer senderId,
			Integer receiverId, String marketType);


}
