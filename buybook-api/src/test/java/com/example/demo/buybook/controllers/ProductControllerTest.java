package com.example.demo.buybook.controllers;

import com.example.demo.buybook.application.ProductService;
import com.example.demo.buybook.domain.Product;
import com.example.demo.buybook.dto.ProductData;
import com.example.demo.buybook.errors.ProductNotFoundException;
import com.example.demo.buybook.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private ProductMapper productMapper;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        Product product = Product.builder()
                .id(1L)
                .title("clean code")
                .publisher("insight")
                .build();

        given(productService.getProductList()).willReturn(List.of(product));

        given(productService.getProduct(1L)).willReturn(product);

        given(productService.getProduct(10L))
                .willThrow(new ProductNotFoundException(10L));

        given(productService.createProduct(any(ProductData.class)))
                .willReturn(product);

        given(productService.updateProduct(eq(1L), any(ProductData.class)))
                .will(invocation -> {
                    Long id = invocation.getArgument(0);

                    ProductData productData = invocation.getArgument(1);

                    return Product.builder()
                            .id(id)
                            .title(productData.getTitle())
                            .publisher(productData.getPublisher())
                            .build();
                });

        given(productService.updateProduct(eq(10L), any(ProductData.class)))
                .willThrow(new ProductNotFoundException(product.getId()));

        given(productService.deleteProduct(10L))
                .willThrow(new ProductNotFoundException(product.getId()));

    }

    @Test
    void ??????_?????????_???????????????_????????????() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/products")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("code")))
                .andDo(document("get-product-list",
                        responseFields(
                                fieldWithPath("[]")
                                        .type(JsonFieldType.ARRAY).description("?????? ?????? ??????"),
                                fieldWithPath("[].id")
                                        .type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("[].title")
                                        .type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("[].publisher")
                                        .type(JsonFieldType.STRING).description("????????? ??????")
                        ))
                );
    }

    @Test
    void ?????????_??????_?????????_????????????() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/products/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("code")))
                .andDo(document("get-product",
                        responseFields(
                                fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("publisher")
                                        .type(JsonFieldType.STRING).description("????????? ??????")
                        )));
    }

}