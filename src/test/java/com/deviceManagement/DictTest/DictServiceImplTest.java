package com.deviceManagement.DictTest;

import com.deviceManagement.dto.ApiResponse;
import com.deviceManagement.dto.DictItemDTO;
import com.deviceManagement.dto.DictResponse;
import com.deviceManagement.entity.Dict;
import com.deviceManagement.repository.DictRepository;
import com.deviceManagement.service.impl.DictServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@SpringBootTest
class DictServiceImplTest {

    @Mock
    private DictRepository dictRepository;

    @InjectMocks
    private DictServiceImpl dictService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDictItemsByTypeCode_Success() {
        // 准备测试数据
        List<Dict> mockDicts = Arrays.asList(
            createMockDict(1L, "USER_TYPE", "admin", 2),
            createMockDict(2L, "USER_TYPE", "user", 1)
        );
        
        when(dictRepository.findByDictTypeCode("USER_TYPE")).thenReturn(mockDicts);

        // 执行测试
        ApiResponse<DictResponse> result = dictService.getDictItemsByTypeCode("USER_TYPE");

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().getData().size());
        // 验证排序（sort字段升序）
        assertEquals(1, result.getData().getData().get(0).getSort());
        assertEquals(2, result.getData().getData().get(1).getSort());
        
        verify(dictRepository, times(1)).findByDictTypeCode("USER_TYPE");
    }

    @Test
    void testGetDictItemsByTypeCode_EmptyResult() {
        when(dictRepository.findByDictTypeCode("INVALID_TYPE")).thenReturn(Arrays.asList());

        ApiResponse<DictResponse> result = dictService.getDictItemsByTypeCode("INVALID_TYPE");

        assertNotNull(result);
        assertEquals(40001, result.getCode()); // DICT_PARAM_ERROR
        
        verify(dictRepository, times(1)).findByDictTypeCode("INVALID_TYPE");
    }

    @Test
    void testGetDictItemsByTypeCode_NullParam() {
        ApiResponse<DictResponse> result = dictService.getDictItemsByTypeCode(null);

        assertNotNull(result);
        assertEquals(40001, result.getCode()); // DICT_PARAM_ERROR
    }

    @Test
    void testGetDictItemsByTypeCode_EmptyParam() {
        ApiResponse<DictResponse> result = dictService.getDictItemsByTypeCode("");

        assertNotNull(result);
        assertEquals(40001, result.getCode()); // DICT_PARAM_ERROR
    }

    private Dict createMockDict(Long dictId, String dictTypeCode, String dictItemName, Integer sort) {
        Dict dict = new Dict();
        dict.setDictId(dictId);
        dict.setDictTypeCode(dictTypeCode);
        dict.setDictItemName(dictItemName);
        dict.setSort(sort);
        return dict;
    }
}
