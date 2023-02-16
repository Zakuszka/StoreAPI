package com.store.storeapi.service.db;

import org.springframework.data.domain.Page;
import com.store.storeapi.pojo.Product;

public interface ProductService {

	Product save(Product product);

	Product getByName(String productName);

	Page<Product> listProducts(String productName, Integer limit, Long offset);

	Page<Product> listActiveProducts(String productName, Integer limit, Long offset);

}
