package com.store.storeapi.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.store.storeapi.bl.ProductBL;
import com.store.storeapi.exception.BaseException;
import com.store.storeapi.pojo.security.AuthUserDetails;
import com.store.storeapi.ws.error.ErrorResponse;
import com.store.storeapi.ws.request.ProductRequest;
import com.store.storeapi.ws.response.ProductResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/products")
public class ProductsController {

	private final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

	@Autowired
	private ProductBL productBL;

	@GetMapping(value = "/product/{productName}", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "Get Product by productName", response = ProductResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = ProductResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<ProductResponse> getProduct(@AuthenticationPrincipal AuthUserDetails authUserDetails,
			@ApiParam(required = true, type = "String", name = "productName") @PathVariable("productName") String productName) throws BaseException {

		return new ResponseEntity<>(productBL.getProduct(productName), HttpStatus.OK);
	}

	@PutMapping(value = "/product/{productName}", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
	@ApiOperation(value = "Update Product", response = ProductResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = ProductResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal AuthUserDetails authUserDetails,
			@ApiParam(required = true, type = "String", name = "productName") @PathVariable("productName") String productName,
			@RequestBody ProductRequest productRequest) throws BaseException {

		return new ResponseEntity<>(productBL.updateProduct(authUserDetails, productName, productRequest), HttpStatus.OK);
	}

	@PostMapping(value = "/product", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
	@ApiOperation(value = "Add new Product", response = ProductResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = ProductResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<ProductResponse> addProduct(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody ProductRequest productRequest)
			throws BaseException {

		return new ResponseEntity<>(productBL.addProduct(authUserDetails, productRequest), HttpStatus.CREATED);
	}

	@GetMapping(value = "", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "List products", response = ProductResponse.class, responseContainer = "List")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = ProductResponse.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<List<ProductResponse>> listProducts(@AuthenticationPrincipal AuthUserDetails authUserDetails,
			@ApiParam(required = false, type = "Boolean", name = "activeOnly") @RequestParam(required = false, value = "activeOnly") Boolean activeOnly,
			@ApiParam(required = false, type = "String", name = "productName") @RequestParam(required = false, value = "productName") String productName,
			@ApiParam(required = false, type = "int", name = "limit") @RequestParam(required = false, value = "limit") Integer limit,
			@ApiParam(required = false, type = "int", name = "offset") @RequestParam(required = false, value = "offset") Long offset) throws BaseException {

		return new ResponseEntity<>(productBL.listProducts(activeOnly, productName, limit, offset), HttpStatus.OK);
	}

}
