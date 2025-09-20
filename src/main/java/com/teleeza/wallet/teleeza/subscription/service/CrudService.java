package com.teleeza.wallet.teleeza.subscription.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface CrudService<T> {

	boolean existsById(Long id);

	T findById(Long id);

	T create(T entity);

	T update(T entity);

	void deleteById(Long id);

	Page<T> findAll(Pageable pageable);

}
