package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AddressList;
import com.annular.filmhook.model.AddressListOnSignUp;

@Repository
public interface AddressListOnSignUpRepsitory extends JpaRepository<AddressListOnSignUp, Integer>{

	AddressListOnSignUp save(AddressList addressList);

}
