package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface contactRepository extends JpaRepository<Contact, Integer> {

	// pegination
	@Query("from Contact as c where c.user.id =:userid")

	public Page<Contact> findContactsByUser(@Param("userid") int userid, Pageable pePageable);

	/*
	 * @Query("select u from Contact u where u.email=:email ") public User
	 * getUserByUserName(@Param("email") String email);
	 */

	// Previous custom method
	List<Contact> findByNameContainingAndUser(String name, User user);
}
