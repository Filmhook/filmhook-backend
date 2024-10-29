package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.MarketPlaceChat;

@Repository
public interface MarketPlaceChatRepository extends JpaRepository<MarketPlaceChat,Integer>{


	boolean existsByMarketPlaceSenderIdAndMarketPlaceReceiverId(Integer marketPlaceSenderId, Integer marketPlaceReceiverId);


}
