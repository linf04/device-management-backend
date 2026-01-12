package com.device.management.DictTest;

import com.device.management.controller.DictController;
import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictItemDto;
import com.device.management.service.DictService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DictController.class)
class DictControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DictService dictService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @WithMockUser
    void testGetDictItemsByTypeCode_Success() throws Exception {
        // テストデータの準備
        List<DictItemDto> mockLists = List.of(new DictItemDto(1L, "使用可", 1));
        ApiResponse<List<DictItemDto>> expectedResponse = ApiResponse.success(mockLists);

        // サービス層の返却をシミュレート
        when(dictService.getDictItemsByTypeCode("USER_TYPE")).thenReturn(expectedResponse);

        // リクエストを実行して結果を検証
        mockMvc.perform(get("/dict/items")
                .param("dictTypeCode", "USER_TYPE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"));
    }

    @Test
    @WithMockUser
    void testGetDictItemsByTypeCode_EmptyDictTypeCode() throws Exception {
        // サービス層の返却エラーをシミュレート
        ApiResponse<List<DictItemDto>> errorResponse = ApiResponse.error(
            40001,
            "dictTypeCodeは文字列で指定してください"
        );
        when(dictService.getDictItemsByTypeCode("")).thenReturn(errorResponse);

        // 空のパラメータをテスト
        mockMvc.perform(get("/dict/items")
                .param("dictTypeCode", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40001));
    }

    @Test
    @WithMockUser
    void testGetDictItemsByTypeCode_NullDictTypeCode() throws Exception {
        // nullパラメータをテスト
        mockMvc.perform(get("/dict/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40001));
    }
}
