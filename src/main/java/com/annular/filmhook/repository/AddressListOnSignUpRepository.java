package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AddressList;
import com.annular.filmhook.model.AddressListOnSignUp;

import java.util.List;

@Repository
public interface AddressListOnSignUpRepository extends JpaRepository<AddressListOnSignUp, Integer> {

    AddressListOnSignUp save(AddressList addressList);

	List<AddressListOnSignUp> findByAddressContainingIgnoreCase(String address);

}
