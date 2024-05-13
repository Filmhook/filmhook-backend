package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.BlockWebModel;

public interface BlockService {

	ResponseEntity<?> addBlock(BlockWebModel blockWebModel);

	ResponseEntity<?> getAllBlock();

	String unBlockProfile(BlockWebModel blockWebModel);
}
