package com.store.storeapi.service.db;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.store.storeapi.dao.ProductRepository;
import com.store.storeapi.pojo.Product;
import com.store.storeapi.service.util.OffsetBasedPageRequest;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductRepository productRepository;

	static Specification<Product> onlyActiveProducts() {
		return (product, cq, cb) -> cb.isTrue(product.get("available"));
	}

	static Specification<Product> productNameContains(String productName) {
		return (product, cq, cb) -> cb.like(product.get("name"), "%" + productName + "%");
	}

	static Specification<Product> onlyActiveAndProductNameContains(String productName) {
		return (product, cq, cb) -> cb.and(cb.isTrue(product.get("available")), cb.like(product.get("name"), "%" + productName + "%"));
	}

	@Override
	public Product save(Product product) {
		try {
			return productRepository.saveAndFlush(product);
		} catch (Exception exception) {
			LOGGER.error("Failed to save the Product: ", exception);
			throw exception;
		}
	}

	@Override
	public Product getByName(String productName) {
		try {
			return productRepository.findByName(productName);
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve the Product: ", exception);
			throw exception;
		}
	}

	@Override
	public Page<Product> listProducts(String productName, Integer limit, Long offset) {
		try {
			if (!StringUtils.isEmpty(productName)) {
				return productRepository.findAll(productNameContains(productName), new OffsetBasedPageRequest(offset, limit, Sort.Direction.ASC, "id"));
			} else {
				return productRepository.findAll(new OffsetBasedPageRequest(offset, limit, Sort.Direction.ASC, "id"));
			}
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve the list of Product: ", exception);
			throw exception;
		}
	}

	@Override
	public Page<Product> listActiveProducts(String productName, Integer limit, Long offset) {
		try {
			if (!StringUtils.isEmpty(productName)) {
				return productRepository.findAll(onlyActiveAndProductNameContains(productName),
						new OffsetBasedPageRequest(offset, limit, Sort.Direction.ASC, "id"));
			} else {
				return productRepository.findAll(onlyActiveProducts(), new OffsetBasedPageRequest(offset, limit, Sort.Direction.ASC, "id"));
			}
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve the list of Product: ", exception);
			throw exception;
		}
	}

}
