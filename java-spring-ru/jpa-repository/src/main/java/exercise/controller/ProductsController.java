package exercise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import exercise.model.Product;
import exercise.repository.ProductRepository;
import exercise.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductRepository productRepository;

    // BEGIN
    @GetMapping
    public List<Product> index(
            @RequestParam(required = false, defaultValue = "0") int min,
            @RequestParam(required = false, defaultValue = "0") int max
    ) {
        List<Product> products = new ArrayList<>();
        Sort sortByPriceAsc = Sort.by(Sort.Order.asc("price"));
        if (min == 0 && max == 0) {
            products.addAll(productRepository.findAll(sortByPriceAsc));
        }
        if (min != 0 && max == 0) {
            products.addAll(productRepository.findByPriceGreaterThanEqual(min, sortByPriceAsc));
        }
        if (min == 0 && max != 0) {
            products.addAll(productRepository.findByPriceLessThanEqual(max, sortByPriceAsc));
        }
        if (min != 0 && max != 0) {
            products.addAll(productRepository.findByPriceBetween(min, max, sortByPriceAsc));
        }
        return products;
    }
    // END

    @GetMapping(path = "/{id}")
    public Product show(@PathVariable long id) {

        var product =  productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        return product;
    }
}
