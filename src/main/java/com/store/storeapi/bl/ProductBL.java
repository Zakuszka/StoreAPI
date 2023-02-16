package com.store.storeapi.bl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.store.storeapi.exception.BadRequestException;
import com.store.storeapi.exception.BaseException;
import com.store.storeapi.pojo.Product;
import com.store.storeapi.pojo.security.AuthUserDetails;
import com.store.storeapi.service.db.ProductService;
import com.store.storeapi.util.Constants;
import com.store.storeapi.ws.error.ErrorMessage;
import com.store.storeapi.ws.request.ProductRequest;
import com.store.storeapi.ws.response.ProductResponse;

@Service
public class ProductBL {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductBL.class);

	@Autowired
	private ProductService productService;

	public ProductResponse getProduct(String productName) throws BaseException {

		if (StringUtils.isEmpty(productName)) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PRODUCT_NAME.getCode(), ErrorMessage.MISSING_OR_INVALID_PRODUCT_NAME.getMessage());
		}

		Product product = productService.getByName(productName);

		if (product == null) {
			LOGGER.warn(String.format("[getProduct] Product with name: %s does not exists", productName));
			throw new BadRequestException(ErrorMessage.PRODUCT_NOT_EXISTS.getCode(), ErrorMessage.PRODUCT_NOT_EXISTS.getMessage());
		}

		return new ProductResponse(product);
	}

	public ProductResponse updateProduct(AuthUserDetails authUserDetails, String productName, ProductRequest productRequest) throws BaseException {

		if (productRequest.getPrice() == null) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PRODUCT_PRICE.getCode(), ErrorMessage.MISSING_OR_INVALID_PRODUCT_PRICE.getMessage());
		}

		Product product = productService.getByName(productName);

		if (product == null) {
			LOGGER.warn(String.format("[updateProduct] Product with name: %s does not exists", productName));
			throw new BadRequestException(ErrorMessage.PRODUCT_NOT_EXISTS.getCode(), ErrorMessage.PRODUCT_NOT_EXISTS.getMessage());
		}

		product.setDescription(productRequest.getDescription());
		product.setCategory(productRequest.getCategory());
		product.setPrice(productRequest.getPrice());
		product.setUpdatedBy(authUserDetails.getUserId());
		product.setAvailable(productRequest.isAvailable());
		product = productService.save(product);

		LOGGER.info(String.format("[updateProduct] Successfully updated the Product with name: %s and id: %d", product.getName(), product.getId()));

		return new ProductResponse(product);
	}

	public ProductResponse addProduct(AuthUserDetails authUserDetails, ProductRequest productRequest) throws BaseException {

		validateParameters(productRequest);

		Product product = productService.getByName(productRequest.getName());

		if (product != null) {
			LOGGER.warn(String.format("[addProduct] Product with name: %s already exists with id: %d", productRequest.getName(), product.getId()));
			throw new BadRequestException(ErrorMessage.PRODUCT_WITH_NAME_EXISTS.getCode(), ErrorMessage.PRODUCT_WITH_NAME_EXISTS.getMessage());
		}

		product = new Product();
		product.setName(productRequest.getName());
		product.setDescription(productRequest.getDescription());
		product.setCategory(productRequest.getCategory());
		product.setPrice(productRequest.getPrice());
		product.setCreatedBy(authUserDetails.getUserId());
		product.setAvailable(productRequest.isAvailable());
		product = productService.save(product);

		LOGGER.info(String.format("[addProduct] New Product added with name: %s and id: %d", productRequest.getName(), product.getId()));

		return new ProductResponse(product);
	}

	public List<ProductResponse> listProducts(Boolean activeOnly, String productName, Integer limit, Long offset) throws BaseException {

		if (offset == null) {
			offset = Constants.DEFAULT_PAGING_OFFSET;
		}

		if (limit != null && limit.intValue() == -1) {
			// no limit requested
			limit = Constants.MAXIMUM_PAGING_LIMIT;
		}

		if (limit == null || limit == 0) {
			limit = Constants.DEFAULT_PAGING_LIMIT;
		}

		Page<Product> products = activeOnly == null || activeOnly.booleanValue() ? productService.listActiveProducts(productName, limit, offset)
				: productService.listProducts(productName, limit, offset);

		if (products == null || products.isEmpty()) {
			return Collections.emptyList();
		}

		return products.stream().map(p -> new ProductResponse(p)).collect(Collectors.toList());
	}

	private static void validateParameters(ProductRequest productRequest) throws BaseException {
		if (StringUtils.isEmpty(productRequest.getName())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PRODUCT_NAME.getCode(), ErrorMessage.MISSING_OR_INVALID_PRODUCT_NAME.getMessage());
		}

		if (productRequest.getPrice() == null) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PRODUCT_PRICE.getCode(), ErrorMessage.MISSING_OR_INVALID_PRODUCT_PRICE.getMessage());
		}
	}

}
