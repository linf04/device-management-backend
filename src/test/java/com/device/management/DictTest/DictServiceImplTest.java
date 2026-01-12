package com.device.management.DictTest;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DictItemDto;
import com.device.management.dto.DictTypeGroup;
import com.device.management.entity.Dict;
import com.device.management.repository.DictRepository;
import com.device.management.service.impl.DictServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        // テストデータの準備
        List<Dict> mockDicts = Arrays.asList(
            createMockDict(1L, "USER_TYPE", "admin", 2),
            createMockDict(2L, "USER_TYPE", "user", 1)
        );
        
        when(dictRepository.findByDictTypeCode("USER_TYPE")).thenReturn(mockDicts);

        // テスト実行
        ApiResponse<List<DictTypeGroup>> result = dictService.getDictItems();

        // 結果の検証
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        
        verify(dictRepository, times(1)).findAll();
    }

    @Test
    void testGetDictItemsByTypeCode_EmptyResult() {
        when(dictRepository.findByDictTypeCode("INVALID_TYPE")).thenReturn(Arrays.asList());

        ApiResponse<List<DictTypeGroup>> result = dictService.getDictItems();

        assertNotNull(result);
        assertEquals(40001, result.getCode()); // DICT_PARAM_ERROR
        
        verify(dictRepository, times(1)).findAll();
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
