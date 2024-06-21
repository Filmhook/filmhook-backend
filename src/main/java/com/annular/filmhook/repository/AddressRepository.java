package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AddressList;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressList, Integer> {

    List<AddressList> findByAddressContainingIgnoreCase(String address);
}
