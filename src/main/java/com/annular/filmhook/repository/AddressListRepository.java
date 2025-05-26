package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AddressList;
import com.annular.filmhook.webmodel.UserWebModel;

import java.util.Collection;
import java.util.List;

@Repository
public interface AddressListRepository extends JpaRepository<AddressList, Integer> {

    AddressList save(AddressList addressList);

    List<AddressList> findBySignUpAddressContainingIgnoreCase(String address);

    List<AddressList> findByAuditionAddressContainingIgnoreCase(String address);

	 List<AddressList> findBynewSignUpAddressContainingIgnoreCase(String address);

}
