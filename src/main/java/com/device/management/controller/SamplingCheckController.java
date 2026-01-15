package com.device.management.controller;

import com.device.management.dto.ApiResponse;
import com.device.management.service.SamplingCheckService;
import com.device.management.dto.SamplingCheckDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/security-checks")
public class SamplingCheckController {
    @Autowired
    private SamplingCheckService samplingCheckService;

    //xiaoshuang
    //レポートのエクスポート
    @GetMapping("/export")
    public void exportSamplingChecks(@RequestParam(required = false)String reportCode, HttpServletResponse response) throws IOException {
        samplingCheckService.exportAll(reportCode, response);
    }

    //wangjunxi
    //詳細情報の取得
    @GetMapping("/{sampling_id}")
    public ApiResponse<SamplingCheckDTO> securityCheckQueryById(@PathVariable String sampling_id){
        SamplingCheckDTO samplingCheckDTO = samplingCheckService.findById(sampling_id);                  //Service層のクエリメソッドを呼び出す
        if(samplingCheckDTO==null){                                                                    //返り値に基づいて判断する
            return ApiResponse.error(404,"目標が存在しない");
        }
        else {
            return ApiResponse.success("検索成功",samplingCheckDTO);
        }
    }


    //liujiale
    //安全点検リストを取得
    @GetMapping
    public ApiResponse<List<SamplingCheckDTO>> getSamplingChecks(
            @RequestParam int page,                               //ページ番号を取得
            @RequestParam(defaultValue = "10") int size,         //1ページあたりに表示する情報件数（デフォルトは10件）
            @RequestParam(required = false) String deviceId,     //デバイスIDでフィルタリング可能
            @RequestParam(required = false) String userId) {     //ユーザーIDでフィルタリング可能

        try {
            Page<SamplingCheckDTO> result = samplingCheckService.findAllWithPageAndCondition(page, size, deviceId, userId);    //データの取得を試行
            return ApiResponse.page(
                    result.getContent(),       // 現在のページのデータリスト
                    result.getTotalElements(), // 総件数
                    page,                      // 現在のページ番号
                    size                       // 1ページあたりの件数
            );
        }
        catch (Exception e) {
            return ApiResponse.error(500, "查询失败: " + e.getMessage());    //エラー情報を返却
        }
    }

    //データを新規追加
    @PostMapping
    public ApiResponse<SamplingCheckDTO> addSamplingCheck(@Valid @RequestBody SamplingCheckDTO dto) {
        try{
            return ApiResponse.success("添加成功", samplingCheckService.create(dto));
        }
        catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());      //エラー情報を返却
        }
    }



    //データを更新
    @PutMapping("/{sampling_id}")
    public ApiResponse<SamplingCheckDTO> updateSamplingCheck(
            @PathVariable String sampling_id,             //URLパスからパラメータを取得する
            @Valid @RequestBody SamplingCheckDTO dto) {   //リクエストボディからJSONデータを取得し、DTOに自動でマッピングします。
        try {
            return ApiResponse.success("更新成功", samplingCheckService.update(sampling_id,dto));
        }
        catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    //データを削除
    @DeleteMapping("/{sampling_id}")
    public ApiResponse<Void> deleteSamplingCheck(@PathVariable String sampling_id) {

        try {
            samplingCheckService.delete(sampling_id);                                    //Service層を呼び出してデータを削除する
            return ApiResponse.success("删除成功", null);                      //成功レスポンスを返す
        } catch (RuntimeException e) {
            return ApiResponse.error(404, "删除失败: " + e.getMessage());     //データが存在しません
        }
    }
}
